package com.utad.mais.tfgmaco.pruebas;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import com.utad.mais.tfgmaco.algoritmos.EscaneoRango;
import com.utad.mais.tfgmaco.curva.CurvaElipticaFp;
import com.utad.mais.tfgmaco.operaciones.DoubleAndAdd;
import com.utad.mais.tfgmaco.punto.PuntoFp;

public class PruebaEscaneoRango {
	
	// Metricas para la prueba
	public static class MetricasEscaneoRango {
		public double tiempoIteracionMs;
		public long iteracionesReales;
		public BigInteger iteracionesMaximas;
		public double tiempoPreparacionMs;
		public double tiempoOrdenPuntoMs;
        public double tiempoEncontrarPuntoMs;
        
        // Estimacion temporal para cada casuistica
		public BigDecimal estimacionOptimistaMs;
        public BigDecimal estimacionMediaMs;
        public BigDecimal estimacionPesimistaMs;
	}
	
	public static MetricasEscaneoRango estimarTiempo(CurvaElipticaFp curva) {
		MetricasEscaneoRango metricas = new MetricasEscaneoRango();
		
		// Informacion de la curva
		BigInteger modulo = curva.getModulo();
			
		// Realizamos una primera fase de preparacion, cuyos calculos tambien se deben tener en cuenta para la estimacion
		
		long t0 = System.nanoTime();
		
		PuntoFp puntoP = EscaneoRango.encontrarPunto(curva, BigInteger.ZERO, BigInteger.ZERO);
		long tPunto = System.nanoTime();
		
		BigInteger m0 = EscaneoRango.calcularM0(modulo);
		BigInteger inferiorHasse = EscaneoRango.calcularHasseInferior(modulo);
		BigInteger superiorHasse = EscaneoRango.calcularHasseSuperior(modulo);
		
		long t1 = System.nanoTime();
		
		metricas.tiempoPreparacionMs = (t1 - t0) / 1000000.0;
		
		// Pasos hasta llegar al limite superior de Hasse
		metricas.iteracionesMaximas = superiorHasse.subtract(m0).max(BigInteger.ZERO);
		
		
		// MEDICION DURANTE 90 SEGUNDOS
		
		System.out.println("\nMidiendo el proceso durante 90 segundos...");
		
		long tInicio = System.nanoTime();
		long duracionNs = 90000000000L;
		BigInteger escalar = m0;
		BigInteger escalarAnulador = null;
		metricas.iteracionesReales = 0;
		
		// Bucle durante 90 segundos o hasta que termina todo intervalo
		while (System.nanoTime() - tInicio < duracionNs) {
			// Double and add a partir de m_0 * P hasta encontrar un m tal que m * P = punto infinito
			PuntoFp resultado = DoubleAndAdd.algoritmoDoubleAddAfin(puntoP, escalar);
            
			if (resultado.esPuntoInfinito()) {
                if (escalarAnulador == null) {
                	escalarAnulador = escalar;
                }
            }
			
			escalar = escalar.add(BigInteger.ONE);
            metricas.iteracionesReales++;
            
            // Si se llega al limite superior del intervalo de Hasse, acaba el bucle
            if (escalar.compareTo(superiorHasse) > 0) {
            	break; 
            }
		}
		
		
		// ESTIMACIONES
		
		if (metricas.iteracionesReales == 0) {
			System.out.println("No se ha podido medir ninguna iteracion");
		}
		
		long tFin = System.nanoTime();
		
		// Calculamos la estimacion de la iteracion en milisegundos
		double tiempoTotalMs = (tFin - tInicio) / 1000000.0;
		metricas.tiempoIteracionMs = tiempoTotalMs / metricas.iteracionesReales;
		
		// Si termina el calculo antes de que se acaben los 90 segundos
		boolean intervaloCompleto = escalar.compareTo(superiorHasse) > 0;
			
		
		// MEDICION DE LA OPERACION encontrarOrdenPunto
		
		BigInteger escalarOrden;
		
		if (escalarAnulador != null) {
		    escalarOrden = escalarAnulador;
		} else {
		    escalarOrden = m0;
		}
		
		int repeticiones = 5;
		
		long t2 = System.nanoTime();
        for (int i = 0; i < repeticiones; i++) {
            EscaneoRango.encontrarOrdenPunto(escalarOrden, curva, puntoP);
        }
        long t3 = System.nanoTime();
        
        metricas.tiempoOrdenPuntoMs = ((t3 - t2) / 1000000.0) / repeticiones;

        
        // MEDICION DE LA OPERACION encontrarPunto
        
        metricas.tiempoEncontrarPuntoMs = (tPunto - t0) / 1000000.0;
        
        
		// EXTRAPOLACION
	
        BigDecimal tPreparacion = BigDecimal.valueOf(metricas.tiempoPreparacionMs);
        BigDecimal tIteracion = BigDecimal.valueOf(metricas.tiempoIteracionMs);
        BigDecimal tOrden = BigDecimal.valueOf(metricas.tiempoOrdenPuntoMs);
        BigDecimal tBuscarQ = BigDecimal.valueOf(metricas.tiempoEncontrarPuntoMs);
        BigDecimal maxIteraciones = new BigDecimal(metricas.iteracionesMaximas);
        
        
        // Escenario optimista: 10% del intervalo y sin otros candidatos
        BigDecimal factorOptimista = new BigDecimal("0.10");
        
        // Escenario medio: 50% del intervalo y con un punto adicional
        BigDecimal factorMedio = new BigDecimal("0.50");
        BigDecimal rondasMedio = BigDecimal.ONE;
        
        // Escenario pesimista: 100% del intervalo y con tres puntos adicionales
        BigDecimal factorPesimista = new BigDecimal("1.00");
        BigDecimal rondasPesimista = BigDecimal.valueOf(3);
        
        // Si se ha recorrido todo el intervalo, se emplea el tiempo real
        if (intervaloCompleto) {
    		BigDecimal tReal = BigDecimal.valueOf(tiempoTotalMs);
            
            // Optimista -> solo un punto
            metricas.estimacionOptimistaMs = tPreparacion.add(tReal).add(tOrden);
            
            // Medio -> punto + punto adicional
            metricas.estimacionMediaMs = tPreparacion.add(tReal).add(tOrden.multiply(BigDecimal.valueOf(2))).add(tBuscarQ.multiply(rondasMedio));
            
            // Pesimista -> punto + 3 puntos adicionales
            metricas.estimacionPesimistaMs = tPreparacion.add(tReal).add(tOrden.multiply(BigDecimal.valueOf(4))).add(tBuscarQ.multiply(rondasPesimista));
        
        } else {     // Aqui hay que estimar el tiempo de las iteraciones segun el caso, dependiendo del porcentaje del intervalo que recorran
        	
        	// Optimista -> 10% y solo un punto
        	metricas.estimacionOptimistaMs = tPreparacion.add(tIteracion.multiply(maxIteraciones.multiply(factorOptimista))).add(tOrden);

        	// Medio -> 50% y punto + punto adicional
            metricas.estimacionMediaMs = tPreparacion.add(tIteracion.multiply(maxIteraciones.multiply(factorMedio)))
            		.add(tOrden.multiply(BigDecimal.valueOf(2))).add(tBuscarQ.multiply(rondasMedio));

            // Pesimista -> 100% y punto + 3 puntos adicionales
            metricas.estimacionPesimistaMs = tPreparacion.add(tIteracion.multiply(maxIteraciones.multiply(factorPesimista)))
            		.add(tOrden.multiply(BigDecimal.valueOf(4))).add(tBuscarQ.multiply(rondasPesimista));
        }
        
        return metricas;
	}
	
	public static void imprimirTiempoEstimado(MetricasEscaneoRango metricas) {
		System.out.println("\n===== ESTIMACION METODO ESCANEO DEL RANGO =====\n");
		
		System.out.printf("Tiempo de la fase de preparacion: %.5f ms %n", metricas.tiempoPreparacionMs);
		System.out.printf("Tiempo del proceso encontrarOrdenPunto: %.5f ms %n", metricas.tiempoOrdenPuntoMs);
		System.out.printf("Tiempo del proceso encontrarPunto: %.5f ms %n", metricas.tiempoEncontrarPuntoMs);
		
		System.out.printf("\nIteraciones evaluadas: %d %n", metricas.iteracionesReales);
		System.out.printf("Tiempo medio por iteracion: %.5f ms %n", metricas.tiempoIteracionMs);
		System.out.printf("Iteraciones maximas (intervalo Hasse): %d %n", metricas.iteracionesMaximas);
		
		// Impresion del tiempo total estimado en varias unidades para cada caso
		System.out.println("\nCASO OPTIMISTA - Tiempo total estimado:");
		calculoTiempoCaso(metricas.estimacionOptimistaMs);
		
		System.out.println("\nCASO MEDIO - Tiempo total estimado:");
		calculoTiempoCaso(metricas.estimacionMediaMs);
		
		System.out.println("\nCASO PESIMISTA - Tiempo total estimado:");
		calculoTiempoCaso(metricas.estimacionPesimistaMs);
	}
	
	public static void calculoTiempoCaso(BigDecimal ms) {
		BigDecimal s = ms.divide(new BigDecimal("1000"), 2, RoundingMode.HALF_UP);
		BigDecimal horas = s.divide(new BigDecimal("3600"), 2, RoundingMode.HALF_UP);
		BigDecimal dias = horas.divide(new BigDecimal("24"), 2, RoundingMode.HALF_UP);
		BigDecimal anios = dias.divide(new BigDecimal("365"), 2, RoundingMode.HALF_UP);
		
		System.out.printf("Años: %.2f  -  Dias: %.2f  -  Horas: %.2f  -  Segundos: %.2f  -  Milisegundos: %.2f%n",
				anios.doubleValue(), dias.doubleValue(), horas.doubleValue(), s.doubleValue(), ms.doubleValue());
	}
}