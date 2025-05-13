package apptive.devlog.fileupload.repository;

import apptive.devlog.domain.UploadFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UploadRepository extends JpaRepository<UploadFile, Long> {

}
