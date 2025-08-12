package com.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "system")
public class SystemProperties {
    private int seedlingsCount;
    private Initial initial = new Initial();

    public int getSeedlingsCount() {
        return seedlingsCount;
    }
    public void setSeedlingsCount(int seedlingsCount) {
        this.seedlingsCount = seedlingsCount;
    }

    public Initial getInitial() {
        return initial;
    }
    public void setInitial(Initial initial) {
        this.initial = initial;
    }

    public static class Initial {
        private Nutrient nutrient = new Nutrient();
        private double humidity;
        private double ph;

        public Nutrient getNutrient() {
            return nutrient;
        }
        public void setNutrient(Nutrient nutrient) {
            this.nutrient = nutrient;
        }

        public double getHumidity() {
            return humidity;
        }
        public void setHumidity(double humidity) {
            this.humidity = humidity;
        }

        public double getPh() {
            return ph;
        }
        public void setPh(double ph) {
            this.ph = ph;
        }
    }

    public static class Nutrient {
        private double n;
        private double p;
        private double k;

        public double getN() {
            return n;
        }
        public void setN(double n) {
            this.n = n;
        }

        public double getP() {
            return p;
        }
        public void setP(double p) {
            this.p = p;
        }

        public double getK() {
            return k;
        }
        public void setK(double k) {
            this.k = k;
        }
    }
}