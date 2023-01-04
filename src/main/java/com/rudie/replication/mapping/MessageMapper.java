package com.rudie.replication.mapping;

import com.rudie.replication.dto.MessageDTO;
import com.rudie.replication.model.Message;
import org.mapstruct.Mapper;

import java.util.Set;

@Mapper
public interface MessageMapper {

    MessageDTO map(Message message);
    Message map(MessageDTO messageDto);
    Set<MessageDTO> map(Set<Message> employees);

}
