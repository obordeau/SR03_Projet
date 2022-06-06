package fr.utc.sr03.chat.controller;

import fr.utc.sr03.chat.dao.ChannelRepository;
import fr.utc.sr03.chat.dao.GuestsRepository;
import fr.utc.sr03.chat.dao.UserRepository;
import fr.utc.sr03.chat.model.*;
import org.attoparser.dom.INestableNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
//import java.util.Date;

import javax.swing.event.ChangeEvent;
import java.util.ArrayList;
import java.sql.Date;

import java.util.List;

@RestController
public class UserController {
    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GuestsRepository guestsRepository;
    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/allchannels")
    public List <Channel> getChannelsList() {
        return channelRepository.findAll();
    }
    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/mychannels/{id}")
    public List <Channel> getMyChannels(@PathVariable Integer id) {
        return channelRepository.findByOwner(id);
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/guests/{channel}")
    public List <User> getGuests(@PathVariable Integer channel) {
        List<Guests> guests = guestsRepository.findByChannel(channel);
        List<User> userList = new ArrayList<>();
        for (Guests guest : guests) {
            User newUser = new User();
            newUser.setId(userRepository.getById(guest.getUser()).getId());
            newUser.setFirstName(userRepository.getById(guest.getUser()).getFirstName());
            newUser.setLastName(userRepository.getById(guest.getUser()).getLastName());
            userList.add(newUser);
        }
        return userList;
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/noneguest/{channel}")
    public List <User> getNoneGuest(@PathVariable Integer channel) {
        List<Guests> guests = guestsRepository.findByChannel(channel);
        List<User> userList = userRepository.findAll();
        for (Guests guest : guests) {
            userList.remove(userRepository.getById(guest.getUser()));
        }
        return userList;
    }
    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/userinformations/{id}")
    public User getUserInformations(@PathVariable long id) {
        return userRepository.findById(id);
    }
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/addchannel")
    public Channel addChannel(@RequestBody Channel channel) {
        channel.setOwner(1);
        return channelRepository.save(channel);
    }
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/deletechannel")
    public void deleteUser(@RequestBody Channel channel) {
        channelRepository.deleteChannelById(channel.getId());
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/deleteguest")
    public void deleteGuest(@RequestBody Guests guest) {
        guestsRepository.deleteGuestsByUserAndChannel(guest.getUser(), guest.getChannel());
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/addguest")
    public Guests addGuest(@RequestBody Guests guest) {
        return guestsRepository.save(guest);
    }

/*    @GetMapping("channels/mine")
    public String getMyChannelsList(Model model, WebRequest request) {
        List<Channel> channels = channelRepository.findChannelsByOwner((long) request.getAttribute("user.id", WebRequest.SCOPE_SESSION));
        model.addAttribute("channels", channels);
        return "home_user";
    }*/

    @MessageMapping("/chat/{channel}")
    @SendTo("/topic/messages/{channel}")
    public OutputMessage send(Message message) throws Exception {
        String time = new SimpleDateFormat("HH:mm").format(new java.util.Date());
        return new OutputMessage(message.getFrom(), message.getText(), time);
    }


    /*@GetMapping("channels/invites")
    public String getInviteChannelsList(Model model, WebRequest request) {
        List<Channel> channels = channelRepository.findChannelsByOwner((long) request.getAttribute("user.id", WebRequest.SCOPE_SESSION));
        model.addAttribute("channels", channels);
        return "home_user";*/


}
