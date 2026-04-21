package com.utad.mais.tfgmaco.pruebas;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import com.utad.mais.tfgmaco.algoritmos.MetodoLegendre;
import com.utad.mais.tfgmaco.curva.CurvaElipticaFp;
import com.utad.mais.tfgmaco.operaciones.AritmeticaModular;

public class PruebaSimboloLegendre {
	
	// Metricas para la prueba
	public static class MetricasSimboloLegendre {
		public double tiempoIteracionMs;
		public long iteracionesReales;
		public BigInteger iteracionesMaximas;
		public BigDecimal estimacionTotalMs;
	}
	
	public static MetricasSimboloLegendre estimarTiempo(CurvaElipticaFp curva) {
		MetricasSimboloLegendre metricas = new MetricasSimboloLegendre();
		
		// Informacion de la curva
		BigInteger a = curva.getA();
		BigInteger b = curva.getB();
		BigInteger modulo = curva.getModulo();
		
		// Con el modulo de la curva se calcula el exponente (p-1) / 2
		BigInteger exponente = modulo.subtract(BigInteger.ONE).divide(BigInteger.TWO);
				
		
		// MEDICION DURANTE 90 SEGUNDOS
		System.out.println("\nMidiendo el proceso durante 90 segundos...");
		
		long tInicio = System.nanoTime();
		long duracionNs = 90000000000L;
		BigInteger x = BigInteger.ZERO;
		BigInteger sumaSimboloLegendre = BigInteger.ZERO;
		metricas.iteracionesReales = 0;
		
		// Recorremos todos los posibles valores de x desde 0 hasta modulo-1 hasta que se acaben los 90 segundos
		while (System.nanoTime() - tInicio < duracionNs && x.compareTo(modulo) < 0) {
			
			// Calculamos a_x = x^3 + a * x + b (mod p)
			BigInteger xCubo = AritmeticaModular.exponenciar(x, BigInteger.valueOf(3), modulo);
			BigInteger ax = AritmeticaModular.sumar(
					(BigInteger)(AritmeticaModular.sumar(xCubo, (BigInteger)(AritmeticaModular.multiplicar(a, x, modulo)), modulo)), 
					b, modulo);
	
			// Calculamos el simbolo de Legendre
			int simboloLegendre = MetodoLegendre.calcularSimboloLegendre(ax, exponente, modulo);
			
			// Sumamos los simbolos de Legendre
			sumaSimboloLegendre = sumaSimboloLegendre.add(BigInteger.valueOf(simboloLegendre));
			
			x = x.add(BigInteger.ONE);
            metricas.iteracionesReales++;
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
		if (x.compareTo(modulo) >= 0) {
			System.out.println("Algoritmo terminado durante la medicion");
			metricas.iteracionesMaximas = modulo;
			metricas.estimacionTotalMs = BigDecimal.valueOf(tiempoTotalMs);
			
			return metricas;
		}
			
		
		// EXTRAPOLACION
	
		metricas.iteracionesMaximas = modulo;
		BigDecimal tiempoMedicionMs = BigDecimal.valueOf(tiempoTotalMs);
		
		// Calculamos las iteraciones faltantes
		BigInteger iteracionesRestantes = modulo.subtract(BigInteger.valueOf(metricas.iteracionesReales));
		
		// El tiempo restante sera lo que tarda de media cada iteracion por las iteraciones faltantes
		BigDecimal tIteracion = BigDecimal.valueOf(metricas.tiempoIteracionMs);
		BigDecimal tiempoRestanteMs = tIteracion.multiply(new BigDecimal(iteracionesRestantes));
		
		// El tiempo total es lo que ha tardado la medicion + lo que se estima que falta
        metricas.estimacionTotalMs = tiempoMedicionMs.add(tiempoRestanteMs);
        
        return metricas;
	}
	
	public static void imprimirTiempoEstimado(MetricasSimboloLegendre metricas) {
		System.out.println("\n===== ESTIMACION METODO SIMBOLO DE LEGENDRE =====\n");
		
		System.out.printf("Iteraciones evaluadas: %d %n", metricas.iteracionesReales);
		System.out.printf("Tiempo medio por iteracion: %.5f ms %n", metricas.tiempoIteracionMs);
		System.out.printf("Iteraciones maximas (modulo): %d %n", metricas.iteracionesMaximas);
		
		// Impresion del tiempo total estimado en varias unidades
		BigDecimal ms = metricas.estimacionTotalMs;
		BigDecimal s = ms.divide(new BigDecimal("1000"), 2, RoundingMode.HALF_UP);
		BigDecimal horas = s.divide(new BigDecimal("3600"), 2, RoundingMode.HALF_UP);
		BigDecimal dias = horas.divide(new BigDecimal("24"), 2, RoundingMode.HALF_UP);
		BigDecimal anios = dias.divide(new BigDecimal("365"), 2, RoundingMode.HALF_UP);
		
		System.out.println("\nTiempo total estimado:");
		System.out.printf("Años: %.2f  -  Dias: %.2f  -  Horas: %.2f  -  Segundos: %.2f  -  Milisegundos: %.2f%n",
				anios.doubleValue(), dias.doubleValue(), horas.doubleValue(), s.doubleValue(), ms.doubleValue());
	}
}