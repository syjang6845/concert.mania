package concert.mania.concert.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Objects;

@Getter
@Builder
@AllArgsConstructor
public class Agreement {
    private final boolean termOfService;
    private final boolean privacyPolicy;
    private final boolean marketingOptIn;
    private final boolean receiveNotification;
    private final boolean guardianConsent;

//    public Agreement(boolean termOfService, boolean privacyPolicy, boolean marketingOptIn) {
//        if (!termOfService || !privacyPolicy) { // 필수 동의 사항
//            throw new IllegalArgumentException("Terms of Service and Privacy Policy must be agreed.");
//        }
//
//        this.termOfService = termOfService;
//        this.privacyPolicy = privacyPolicy;
//        this.marketingOptIn = marketingOptIn;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Agreement agreement = (Agreement) o;
        return termOfService == agreement.termOfService &&
                privacyPolicy == agreement.privacyPolicy &&
                marketingOptIn == agreement.marketingOptIn &&
                receiveNotification == agreement.receiveNotification &&
                guardianConsent == agreement.guardianConsent;
    }

    @Override
    public int hashCode() {
        return Objects.hash(termOfService, privacyPolicy, marketingOptIn, receiveNotification, guardianConsent);
    }
}
