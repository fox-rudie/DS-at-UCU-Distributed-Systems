package com.rudie.replication.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Setter
@Getter
@ToString
@NoArgsConstructor
@Accessors(chain = true)
public class MessageDTO {

    @NotBlank(message = "Message field is required")
    private String message;

}
