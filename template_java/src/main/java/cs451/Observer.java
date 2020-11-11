package cs451;

import cs451.Messages.Message;

public interface Observer {
    void deliver(Message message);
}
