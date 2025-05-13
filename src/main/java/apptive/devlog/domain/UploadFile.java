package apptive.devlog.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class UploadFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Long id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String serverFileName;

    @Column(nullable = false, length = 65535)
    @Lob
    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    public UploadFile(String fileName, String serverFileName, String url, Post post, Member member) {
        this.fileName = fileName;
        this.serverFileName = serverFileName;
        this.url = url;
        this.post = post;
        this.member = member;
    }



}
