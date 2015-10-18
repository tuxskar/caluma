/**
 * Created by tuxskar on 20/05/15.
 */
package prod.tuxskar.caluma.ws;

import java.util.Date;

import prod.tuxskar.caluma.ws.models.users.MessageSender;

public class MessageToSubject {
    private String message;
    private Date modified;
    private long id;
    private MessageSender sender;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public MessageSender getSender() {
        return sender;
    }

    public void setSender(MessageSender sender) {
        this.sender = sender;
    }
}
