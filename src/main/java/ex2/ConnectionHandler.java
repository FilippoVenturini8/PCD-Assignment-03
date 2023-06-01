package ex2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import ex2.messages.*;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeoutException;

public class ConnectionHandler {

    private static final String EXCHANGE_NAME = "pixelGrid_exchange";
    private final Channel channel;
    private final Connection connection;
    private final ObjectMapper mapper = new ObjectMapper();
    private boolean isGridInitialized;
    private final String clientId = String.valueOf(new Random().nextInt());
    private final BrushManager brushManager;
    private final PixelGrid grid;
    private final PixelGridView gridView;

    public static int randomColor() {
        Random rand = new Random();
        return rand.nextInt(256 * 256 * 256);
    }

    public ConnectionHandler(BrushManager brushManager, PixelGrid grid, PixelGridView gridView) throws IOException, TimeoutException {
        this.brushManager = brushManager;
        this.grid = grid;
        this.gridView = gridView;

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        connection = factory.newConnection();
        channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "direct");
        String queueName = channel.queueDeclare().getQueue();
        System.out.println();

        channel.queueBind(queueName, EXCHANGE_NAME, "join");
        channel.queueBind(queueName, EXCHANGE_NAME, "leave");
        channel.queueBind(queueName, EXCHANGE_NAME, "newPosition");
        channel.queueBind(queueName, EXCHANGE_NAME, "newColor");
        channel.queueBind(queueName, EXCHANGE_NAME, "newPaintedPixel");
        channel.queueBind(queueName, EXCHANGE_NAME, "initInfo-"+this.clientId);
        channel.queueBind(queueName, EXCHANGE_NAME, "gridRequest-"+this.clientId);
        channel.queueBind(queueName, EXCHANGE_NAME, "gridResponse-"+this.clientId);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            //System.out.println(" [x] Received '" + delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");

            switch (delivery.getEnvelope().getRoutingKey()){
                case "join" -> this.onJoin(mapper.readValue(delivery.getBody(), JoinMessage.class));
                case "leave" -> this.onLeave(mapper.readValue(delivery.getBody(), Message.class));
                case "newPosition" -> this.onNewPosition(mapper.readValue(delivery.getBody(), MovedMessage.class));
                case "newColor" -> this.onNewColor(mapper.readValue(delivery.getBody(), ColorChangedMessage.class));
                case "newPaintedPixel" -> this.onNewPaintedPixel(mapper.readValue(delivery.getBody(), PaintMessage.class));
            }
            if(delivery.getEnvelope().getRoutingKey().equals("initInfo-"+this.clientId)){
                this.onInitInfo(mapper.readValue(delivery.getBody(), InitMessage.class));
            }else if(delivery.getEnvelope().getRoutingKey().equals("gridRequest-"+this.clientId)){
                this.onGridRequest(mapper.readValue(delivery.getBody(), Message.class));
            }else if(delivery.getEnvelope().getRoutingKey().equals("gridResponse-"+this.clientId)){
                this.onGridResponse(mapper.readValue(delivery.getBody(), GridMessage.class));
            }
            this.gridView.refresh();
        };

        channel.basicConsume(queueName, true, deliverCallback, t -> {});
    }

    public void connect() throws IOException {
        //JoinMessage join = new JoinMessage(connection.getAddress().getHostName() + ":" +  connection.getPort(), randomColor());
        JoinMessage join = new JoinMessage(this.clientId, randomColor());
        channel.basicPublish(EXCHANGE_NAME, "join", null, this.mapper.writeValueAsBytes(join));
    }

    public void disconnect() throws IOException {
        Message disconnect = new Message(this.clientId);
        channel.basicPublish(EXCHANGE_NAME, "leave", null, this.mapper.writeValueAsBytes(disconnect));
    }

    public void sendNewPosition(int x, int y) throws IOException {
        MovedMessage movedMessage = new MovedMessage(this.clientId, x, y);
        channel.basicPublish(EXCHANGE_NAME, "newPosition", null, this.mapper.writeValueAsBytes(movedMessage));
    }

    public void sendNewColor(int newColor) throws IOException{
        ColorChangedMessage colorChangedMessage = new ColorChangedMessage(this.clientId, newColor);
        channel.basicPublish(EXCHANGE_NAME, "newColor", null, this.mapper.writeValueAsBytes(colorChangedMessage));
    }

    public void sendPaintedPixel(int x, int y) throws IOException{
        PaintMessage paintMessage = new PaintMessage(this.clientId, x, y);
        channel.basicPublish(EXCHANGE_NAME, "newPaintedPixel", null, this.mapper.writeValueAsBytes(paintMessage));
    }

    private void onJoin(JoinMessage joinMessage) throws IOException {
        brushManager.addBrush(new BrushManager.Brush(joinMessage.getId(),10, 10, joinMessage.getColor()));

        if(!joinMessage.getId().equals(clientId)){
            BrushManager.Brush myBrush = this.brushManager.getBrush(this.clientId);
            InitMessage initMessage = new InitMessage(clientId, myBrush.getX(), myBrush.getY(), myBrush.getColor());
            channel.basicPublish(EXCHANGE_NAME, "initInfo-"+joinMessage.getId(), null, this.mapper.writeValueAsBytes(initMessage));
        }

    }

    private void onLeave(Message leaveMessage){
        brushManager.removeBrush(this.brushManager.getBrush(leaveMessage.getId()));
    }

    private void onNewPosition(MovedMessage movedMessage){
        brushManager.getBrush(movedMessage.getId()).updatePosition(movedMessage.getX(), movedMessage.getY());
    }

    private void onNewColor(ColorChangedMessage colorChangedMessage){
        brushManager.getBrush(colorChangedMessage.getId()).setColor(colorChangedMessage.getNewColor());
    }

    private void onNewPaintedPixel(PaintMessage paintMessage){
        grid.set(paintMessage.getX(), paintMessage.getY(), brushManager.getBrush(paintMessage.getId()).getColor());
    }

    private void onInitInfo(InitMessage initMessage) throws IOException {
        if(!this.isGridInitialized){
            this.isGridInitialized = true;
            Message gridRequest = new Message(this.clientId);
            channel.basicPublish(EXCHANGE_NAME, "gridRequest-"+initMessage.getId(), null, this.mapper.writeValueAsBytes(gridRequest));
        }
        brushManager.addBrush(new BrushManager.Brush(initMessage.getId(), initMessage.getX(), initMessage.getY(), initMessage.getColor()));
    }

    private void onGridRequest(Message gridRequest) throws IOException {
        GridMessage gridMessage = new GridMessage(this.clientId, this.grid.toList());
        channel.basicPublish(EXCHANGE_NAME, "gridResponse-"+gridRequest.getId(), null, this.mapper.writeValueAsBytes(gridMessage));
    }

    private void onGridResponse(GridMessage gridResponse) throws IOException {
        gridResponse.getPixels().forEach(p -> this.grid.set(p.getX(), p.getY(), p.getColor()));
    }
}
