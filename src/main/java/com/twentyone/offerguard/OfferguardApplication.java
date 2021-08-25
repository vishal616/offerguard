package com.twentyone.offerguard;

import com.twentyone.offerguard.models.Offer18VendorModel;
import com.twentyone.offerguard.offerVendors.Offer18Vendor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class OfferguardApplication {

    public static void main(String[] args) throws IOException {

        SpringApplication.run(OfferguardApplication.class, args);

        Offer18VendorModel offer18VendorModel = new Offer18VendorModel(null, null, "IN", "active", "1", "177664");

        Offer18Vendor.getOffers(offer18VendorModel);

    }

}
