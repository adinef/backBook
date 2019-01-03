package net.fp.backBook.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatePairDto {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
