package info.ejava.examples.db.jpa.songs.dto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JacksonXmlRootElement(localName="song", namespace = "urn:ejava.db-repo.songs")
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "song", namespace = "urn:ejava.db-repo.songs")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SongDTO {

    @JacksonXmlProperty(isAttribute = true)
    @XmlAttribute
    private int id;
    private String title;
    private String artist;
    @XmlJavaTypeAdapter(LocalDateJaxbAdapter.class)
    private LocalDate released;
    

    public static class LocalDateJaxbAdapter extends XmlAdapter<String, LocalDate> {

        @Override
        public String marshal(LocalDate timeStamp) throws Exception {
            return null!=timeStamp ? DateTimeFormatter.ISO_LOCAL_DATE.format(timeStamp) : null;
        }

        @Override
        public LocalDate unmarshal(String text) throws Exception {
            return null!=text ? LocalDate.parse(text,DateTimeFormatter.ISO_LOCAL_DATE) : null; 
        }
        
    }
}
