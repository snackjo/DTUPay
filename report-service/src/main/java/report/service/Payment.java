package report.service;

import lombok.*;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Payment {
    @NonNull String merchantDtuPayId;
    @NonNull Token customerToken;
    @NonNull int amount;
    String customerDtuPayId;
}
