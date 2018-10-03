package pe.albatross.zelpers.miscelanea;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class FrenchMethod {

    private BigDecimal unoMasInteresPowCuotas;
    private BigDecimal oneMasInteres;

    private BigDecimal quota;
    private List<BigDecimal> capitales;
    private List<BigDecimal> amortizaciones;
    private List<BigDecimal> intereses;
    private List<BigDecimal> cuotas;

    private BigDecimal capital;
    private BigDecimal interes;
    private Integer cantidadCuotas;
    private Integer redondeoCuota;
    private Integer redondeoCalculo = 20;

    public FrenchMethod(BigDecimal capital, BigDecimal interes, Integer cuotas, Integer redondeo) {
        this.redondeoCuota = redondeo;
        this.capital = capital;
        this.interes = interes.divide(new BigDecimal(12), this.redondeoCalculo, RoundingMode.HALF_UP);
        this.cantidadCuotas = cuotas;
        this.oneMasInteres = this.interes.add(BigDecimal.ONE);
        this.unoMasInteresPowCuotas = oneMasInteres.pow(cantidadCuotas);

        this.capitales = new ArrayList();
        this.amortizaciones = new ArrayList();
        this.intereses = new ArrayList();
        this.cuotas = new ArrayList();
        this.calcularMontos();
    }

    private void calcularMontos() {

        capitales.clear();
        amortizaciones.clear();
        intereses.clear();
        cuotas.clear();

        if (this.interes.compareTo(BigDecimal.ZERO) == 0) {
            this.quota = capital.divide(new BigDecimal(cantidadCuotas), this.redondeoCuota, RoundingMode.HALF_UP);
            BigDecimal resto = capital.add(BigDecimal.ZERO);
            for (int i = 1; i <= this.cantidadCuotas; i++) {
                if (i < this.cantidadCuotas) {
                    capitales.add(quota);
                    amortizaciones.add(quota);
                    cuotas.add(quota);
                    resto = resto.subtract(quota);
                } else {
                    capitales.add(resto);
                    amortizaciones.add(resto);
                    cuotas.add(resto);
                }
                intereses.add(BigDecimal.ZERO);
            }

        } else {
            this.quota = capital.multiply(interes).multiply(unoMasInteresPowCuotas).divide(unoMasInteresPowCuotas.subtract(BigDecimal.ONE), this.redondeoCuota, RoundingMode.HALF_UP);

            for (int i = 0; i < this.cantidadCuotas; i++) {
                BigDecimal capitalCuota = getCapital(i + 1);
                BigDecimal amortiza = getAmortizacion(i + 1);
                BigDecimal interesCuota = getInteres(i + 1);
                capitales.add(capitalCuota);
                amortizaciones.add(amortiza);
                intereses.add(interesCuota);
                cuotas.add(quota);
            }
        }

    }

    private BigDecimal getCapital(int nroCuota) {
        BigDecimal unoMasInteresPowNroCuotaMenosUno = oneMasInteres.pow(nroCuota - 1);
        return capital.multiply(unoMasInteresPowCuotas.subtract(unoMasInteresPowNroCuotaMenosUno)).divide(unoMasInteresPowCuotas.subtract(BigDecimal.ONE), this.redondeoCalculo, RoundingMode.HALF_UP);
    }

    private BigDecimal getInteres(int nroCuota) {
        BigDecimal unoMasInteresPowNroCuotaMenosUno = oneMasInteres.pow(nroCuota - 1);
        return capital.multiply(interes).multiply(unoMasInteresPowCuotas.subtract(unoMasInteresPowNroCuotaMenosUno)).divide(unoMasInteresPowCuotas.subtract(BigDecimal.ONE), this.redondeoCalculo, RoundingMode.HALF_UP);
    }

    private BigDecimal getAmortizacion(int nroCuota) {
        BigDecimal unoMasInteresPowNroCuotaMenosUno = oneMasInteres.pow(nroCuota - 1);
        return capital.multiply(interes).multiply(unoMasInteresPowNroCuotaMenosUno).divide(unoMasInteresPowCuotas.subtract(BigDecimal.ONE), this.redondeoCalculo, RoundingMode.HALF_UP);
    }

    public BigDecimal getCapital() {
        return capital;
    }

    public void setCapital(BigDecimal capital) {
        this.capital = capital;
        this.calcularMontos();
    }

    public BigDecimal getInteres() {
        return interes;
    }

    public void setInteres(BigDecimal interes) {
        this.interes = interes.divide(new BigDecimal(12), this.redondeoCalculo, RoundingMode.HALF_UP);
        this.calcularMontos();
    }

    public Integer getCantidadCuotas() {
        return cantidadCuotas;
    }

    public void setCantidadCuotas(Integer cantidadCuotas) {
        this.cantidadCuotas = cantidadCuotas;
        this.calcularMontos();
    }

    public List<BigDecimal> getCapitales() {
        return capitales;
    }

    public List<BigDecimal> getAmortizaciones() {
        return amortizaciones;
    }

    public BigDecimal getQuota() {
        return quota;
    }

    public List<BigDecimal> getIntereses() {
        return intereses;
    }

    public List<BigDecimal> getCuotas() {
        return cuotas;
    }

}
