package com.rudie.replication.registry.main;

import com.rudie.replication.model.Node;
import com.rudie.replication.registry.RegistrationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
@ConditionalOnProperty(prefix = "replication", name = "main", havingValue = "true")
public class MainNodeRegistrationService implements RegistrationService {

    NodeManager nodeManager;

    @Override
    public void register(Node secondaryNode) {
        nodeManager.register(secondaryNode);
    }
}
