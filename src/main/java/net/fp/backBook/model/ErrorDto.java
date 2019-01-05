package net.fp.backBook.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.beans.Transient;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDto {
    private String error;

    @Transient
    public String getJsonMessage() {
        return "{\"error\": " + (error != null? "\"" + error + "\"}" : "\"[UNSPECIFIED ERROR MESSAGE]\"}");
    }
}
