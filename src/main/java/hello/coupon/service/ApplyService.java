package hello.coupon.service;

import hello.coupon.domain.Coupon;
import hello.coupon.producer.CouponCreateProducer;
import hello.coupon.repository.AppliedUserRepository;
import hello.coupon.repository.CouponCountRepository;
import hello.coupon.repository.CouponRepository;
import org.springframework.stereotype.Service;

@Service
public class ApplyService {

    private final CouponRepository couponRepository;

    private final CouponCountRepository couponCountRepository;

    private final CouponCreateProducer couponCreateProducer;

    private final AppliedUserRepository appliedUserRepository;

    public ApplyService(CouponRepository couponRepository, CouponCountRepository couponCountRepository, CouponCreateProducer couponCreateProducer,
        AppliedUserRepository appliedUserRepository) {
        this.couponRepository = couponRepository;
        this.couponCountRepository = couponCountRepository;
        this.couponCreateProducer = couponCreateProducer;
        this.appliedUserRepository = appliedUserRepository;
    }

    public void apply(Long userId) {
        // DB 사용
//        long count = couponRepository.count();
        // 레디스 사용
        long count = couponCountRepository.increment();

        if (count > 100) {
            return;
        }
        // DB 저장
//        couponRepository.save(new Coupon(userId));
        couponCreateProducer.create(userId);
    }

    /**
     * 1인당 1개만 발급
     */
    public void apply2(Long userId) {
        // 레디스 set 사용
        Long apply = appliedUserRepository.add(userId);

        if (apply != 1) {
            return;
        }

        // 레디스 사용
        long count = couponCountRepository.increment();

        if (count > 100) {
            return;
        }
        // DB 저장
        couponCreateProducer.create(userId);
    }
}
