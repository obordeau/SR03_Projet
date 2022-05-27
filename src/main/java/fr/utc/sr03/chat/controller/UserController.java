package fr.utc.sr03.chat.controller;

import fr.utc.sr03.chat.dao.ChannelRepository;
import fr.utc.sr03.chat.dao.UserRepository;
import fr.utc.sr03.chat.model.Channel;
import fr.utc.sr03.chat.model.Message;
import fr.utc.sr03.chat.model.OutputMessage;
import fr.utc.sr03.chat.model.User;
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
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("user")
public class UserController {
    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("channels")
    public String getChannelsList(Model model) {
        List<Channel> channels = channelRepository.findAll();
        List owners = new ArrayList<>();
        for (int i = 0; i < channels.size(); i++) {
            owners.add(userRepository.getById((long) channels.get(i).getOwner()).getFirstName());
        }
        model.addAttribute("channels", channels);
        model.addAttribute("owners", owners);
        return "home_user";
    }

    @GetMapping("channels/mine")
    public String getMyChannelsList(Model model, WebRequest request) {
        List<Channel> channels = channelRepository.findChannelsByOwner((long) request.getAttribute("user.id", WebRequest.SCOPE_SESSION));
        model.addAttribute("channels", channels);
        return "home_user";
    }

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public OutputMessage send(Message message) throws Exception {
        String time = new SimpleDateFormat("HH:mm").format(new Date());
        return new OutputMessage(message.getFrom(), message.getText(), time);
    }


    /*@GetMapping("channels/invites")
    public String getInviteChannelsList(Model model, WebRequest request) {
        List<Channel> channels = channelRepository.findChannelsByOwner((long) request.getAttribute("user.id", WebRequest.SCOPE_SESSION));
        model.addAttribute("channels", channels);
        return "home_user";*/


}
