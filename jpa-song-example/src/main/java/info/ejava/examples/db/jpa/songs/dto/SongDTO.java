package info.ejava.examples.db.jpa.songs.dto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor // unmarshalling
@AllArgsConstructor 
@Builder

public class SongDTO {
    
    private int id;
    private String title;
    private String artist;


    
    private LocalDate released;


    public static class LocalDateJaxbAdapter extends XmlAdapter<String, LocalDate> {

        @Override
        public String marshal(LocalDate timeStamp) throws Exception {
            return null != timeStamp ? DateTimeFormatter.ISO_LOCAL_DATE.format(timeStamp): null;
        }

        @Override
        public LocalDate unmarshal(String text) throws Exception {
            return null != text ? LocalDate.parse(text, DateTimeFormatter.ISO_LOCAL_DATE) : null;
        }

    }

}
