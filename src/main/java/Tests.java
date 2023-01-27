import http_client.MyHttpClient;

public class Tests {
    public static void main(String[] args) throws Exception {

        //task 1
        MyHttpClient.createNewUser();
        MyHttpClient.editUser(9);
        MyHttpClient.deleteUser(3);
        MyHttpClient.getAllUsers();
        MyHttpClient.getUserById(2);
        MyHttpClient.getUserByUsername("Kamren");

        //task 2
        MyHttpClient.getCommentsToLastUsersPostByUserId(8);

        //task 3
        MyHttpClient.getOpenTasksByUserId(5);


    }
}
