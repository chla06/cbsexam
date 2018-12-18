package com.cbsexam;

import com.google.gson.Gson;
import controllers.UserController;
import java.util.ArrayList;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import model.User;
import utils.Log;
import cache.UserCache;

import static utils.Encryption.encryptDecryptXOR;

@Path("user")
public class UserEndpoints {

  /**
   * @param idUser
   * @return Responses
   */
  @GET
  @Path("/{idUser}")
  public Response getUser(@PathParam("idUser") int idUser) {

    // Use the ID to get the user from the controller.
    User user = UserController.getUser(idUser);

    // solved TODO: Add Encryption to JSON
    // Convert the user object to json in order to return the object
    String json = new Gson().toJson(user);
    json = encryptDecryptXOR(json);

    // Return the user with the status code 200
    // TODO: What should happen if something breaks down?
    if (user != null) {
      return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
    } else {
      return Response.status(400).entity("Could not get user").build();
    }
  }

  /**
   * @return Responses
   */
  @GET
  @Path("/")
  public Response getUsers() {

    // Write to log that we are here
    Log.writeLog(this.getClass().getName(), this, "Get all users", 0);

    // Get a list of users
    ArrayList<User> users = UserController.getUsers();

    // TODO: Add Encryption to JSON
    // Transfer users to json in order to return it to the user
    String json = new Gson().toJson(users);
    json = encryptDecryptXOR(json);

    // Return the users with the status code 200
    if (users != null) {
      return Response.status(200).type(MediaType.APPLICATION_JSON).entity(json).build();
    } else {
      return Response.status(400).entity("No users obtained").build();
    }
  }

  @POST
  @Path("/create")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response createUser(String body) {


    User user = new Gson().fromJson(body, User.class);
    // Use the controller to add the user
    User createUser = UserController.createUser(user);

    // Get the user back with the added ID and return it to the user
    String json = new Gson().toJson(createUser);

    // Return the data to the user
    if (createUser != null) {
      // Return a response with status 200 and JSON as type
      return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
    } else {
      return Response.status(400).entity("Could not create new user").build();
    }
  }

  // TODO: Make the system able to login users and assign them a token to use throughout the system.
  @POST
  @Path("/login")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response loginUser(String body) {

    User user = new Gson().fromJson(body, User.class);
    // Read the json from body and transfer it to a user class
    String token = UserController.loginUser(user);
    // Return a response with status 200 and JSON as type
    if (token != null) {
      return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(token).build();
    } else

    {
      return Response.status(400).entity("Could'nt create user").build();
    }
  }


  // TODO: Make the system able to delete users
  @DELETE
  @Path("/delete")
  public Response deleteUser(String body) {

    User user = new Gson().fromJson(body, User.class);
    //    // Read the json from body and transfer it to a user class
    if (UserController.deleteUser(user.getToken()))
    // Return a response with status 200 and JSON as type
    {
      return Response.status(200).entity("User is deleted").build();
    } else {

      return Response.status(400).entity("Could'nt create user").build();

    }
  }

  // TODO: Make the system able to update users
  @POST
  @Path("/updateUser")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response updateUser(String body) {


    User user = new Gson().fromJson(body, User.class);
    if (UserController.updateUser(user, user.getToken())){
      UserCache uc = new UserCache();
      uc.getUsers(true);

    // Return a response with status 200 and JSON as type
    return Response.status(200).entity("User updated").build();
  } else {
    return Response.status(400).entity("Could'nt update user").build();
  }
}
}
