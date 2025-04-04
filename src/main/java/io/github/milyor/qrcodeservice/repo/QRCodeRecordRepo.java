package io.github.milyor.qrcodeservice.repo;

import io.github.milyor.qrcodeservice.entity.QRCodeRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QRCodeRecordRepo extends JpaRepository<QRCodeRecord, Long> {

}
