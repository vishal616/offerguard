package com.twentyone.offerguard;

import com.twentyone.offerguard.models.Offer18VendorModel;
import com.twentyone.offerguard.offerVendors.Offer18Vendor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OfferguardApplication {

    public static void main(String[] args) {

        SpringApplication.run(OfferguardApplication.class, args);

        Offer18VendorModel offer18VendorModel = new Offer18VendorModel();

        Offer18Vendor.getOffers(offer18VendorModel);

    }

}
