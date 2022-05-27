package fr.utc.sr03.chat.controller;

import fr.utc.sr03.chat.dao.ChannelRepository;
import fr.utc.sr03.chat.dao.UserRepository;
import fr.utc.sr03.chat.model.Channel;
import fr.utc.sr03.chat.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

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

    @GetMapping("/allchannels")
    public List <Channel> getChannelsList() {
        return channelRepository.findAll();
    }
    @PostMapping("/addchannel")
    public Channel addChannel(@RequestBody Channel channel) {
        channel.setOwner(1);
        channel.setStartDate(new Date(System.currentTimeMillis()));
        channel.setEndDate(new Date(System.currentTimeMillis()));
        System.out.println(channel);
        return channelRepository.save(channel);
    }

    @GetMapping("channels/mine")
    public String getMyChannelsList(Model model, WebRequest request) {
        List<Channel> channels = channelRepository.findChannelsByOwner((long) request.getAttribute("user.id", WebRequest.SCOPE_SESSION));
        model.addAttribute("channels", channels);
        return "home_user";
    }



    /*@GetMapping("channels/invites")
    public String getInviteChannelsList(Model model, WebRequest request) {
        List<Channel> channels = channelRepository.findChannelsByOwner((long) request.getAttribute("user.id", WebRequest.SCOPE_SESSION));
        model.addAttribute("channels", channels);
        return "home_user";*/


}
