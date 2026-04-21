package com.utad.mais.tfgmaco.polinomios;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

import com.utad.mais.tfgmaco.curva.CurvaElipticaFp;
import com.utad.mais.tfgmaco.operaciones.AritmeticaModular;

/* IDEA: eliminar la variable "y" de los polinomios de division, para poder emplear las funciones de Polinomio en x
 * Para ello, se utilizan unos polinomios f modificados 
 * 
 * Como en Schoof se emplearan solo los impares porque l son primos (excepto para l=2, que se trata a parte),
 * entonces podemos emplear f como polinomio de division, ya que para k impar se cumple que f_k = psi_k
 * 
 * NOTA: los polinomios f_k con k par no se podrian emplear como polinomios de division psi_k
 * Solo se utilizan para calculos intermedios
 */

public class PolinomioDivision {
	// Historial que guarda el numero k con su correspondiente polinomio
	// Asi, evitamos calcularlo cada vez
	private static HashMap<BigInteger, Polinomio> historial = new HashMap<>();
	private static Polinomio FCuadrado;
	
	public static void calcularFCuadrado(Polinomio curva) {
		// F = 4 * Curva -> F = 4 * (x^3 + Ax + B)

		ArrayList<BigInteger> coeficientes = new ArrayList<>();
		
		for (int i = 0; i < curva.getCoeficientes().size(); i++) {
			// Multiplicamos los coeficientes de la curva por 4
			coeficientes.add(AritmeticaModular.multiplicar(curva.getCoeficientes().get(i), BigInteger.valueOf(4), curva.getModulo())); 
		}
		
		Polinomio F = new Polinomio(curva.getModulo(), coeficientes);
		F.limpiarPolinomio();
		FCuadrado = Polinomio.multiplicar(F, F);
		FCuadrado.limpiarPolinomio();
	}
	
	public static void limpiarHistorial() {
		historial.clear();
		FCuadrado = null;
	}
	
	public static Polinomio calcularPolinomioDivision(BigInteger k, BigInteger modulo, CurvaElipticaFp curva) {
		Polinomio resultado;
		
		// Comprobamos si ya estaba calculado antes
		if (historial.containsKey(k)) {
			return historial.get(k);
		}
		
		
		// CASOS BASE
		if (k.equals(BigInteger.ZERO)) {     // f_0 = 0 = psi_0
			Polinomio f_0 = Polinomio.crearMonomio(modulo, 0, BigInteger.ZERO);
			historial.put(k, f_0);
			return f_0;
		}
		
		if (k.equals(BigInteger.ONE)) {     // f_1 = 1 = psi_1
			Polinomio f_1 = Polinomio.crearMonomio(modulo, 0, BigInteger.ONE);
			historial.put(k, f_1);
			return f_1;
		}
		
		if (k.equals(BigInteger.TWO)) {    // f_2 = 1
			Polinomio f_2 = Polinomio.crearMonomio(modulo, 0, BigInteger.ONE);
			historial.put(k, f_2);
			return f_2;
		}
		
		if (k.equals(BigInteger.valueOf(3))) {    // f_3 = psi_3
			Polinomio f_3 = construirF3(curva.getA(), curva.getB(), modulo);
			historial.put(k, f_3);
			return f_3;
		}
		
		if (k.equals(BigInteger.valueOf(4))) {    // f_4 = psi_4 / psi_2
			Polinomio f_4 = construirF4(curva.getA(), curva.getB(), modulo);
			historial.put(k, f_4);
			return f_4;
		}
		
		
		// CASO 1: si k es impar (k = 2 * n + 1), entonces f_k = psi_k  --> los que empleamos para Schoof
		if (k.mod(BigInteger.TWO).equals(BigInteger.ONE)) {
			// n = (k - 1) / 2
			BigInteger n = k.subtract(BigInteger.ONE).divide(BigInteger.TWO);
			
			// Polinomios necesarios
			Polinomio f_n = calcularPolinomioDivision(n, modulo, curva);
			Polinomio f_nmenos1 = calcularPolinomioDivision(n.subtract(BigInteger.ONE), modulo, curva);
			Polinomio f_nmas1 = calcularPolinomioDivision(n.add(BigInteger.ONE), modulo, curva);
			Polinomio f_nmas2 = calcularPolinomioDivision(n.add(BigInteger.TWO), modulo, curva);
			
			Polinomio f_nCuadrado = Polinomio.multiplicar(f_n, f_n);
			Polinomio f_nCubo = Polinomio.multiplicar(f_nCuadrado, f_n);
			Polinomio f_nmas1Cuadrado = Polinomio.multiplicar(f_nmas1, f_nmas1);
			Polinomio f_nmas1Cubo = Polinomio.multiplicar(f_nmas1Cuadrado, f_nmas1);
		
			// Existen dos formulas diferentes dependiendo de si n es par o impar
			if (n.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {   // n par
				
				// f_2n+1 = F^2 * f_n+2 * (f_n)^3 - f_n-1 * (f_n+1)^3
				
				Polinomio ladoIzquierdo = Polinomio.multiplicar(Polinomio.multiplicar(FCuadrado, f_nmas2), f_nCubo);
				Polinomio ladoDerecho = Polinomio.multiplicar(f_nmenos1, f_nmas1Cubo);
				
				resultado = Polinomio.restar(ladoIzquierdo, ladoDerecho);
				
			} else {   // n impar
				
				// f_2n+1 = f_n+2 * (f_n)^3 - F^2 * f_n-1 * (f_n+1)^3
				
				Polinomio ladoIzquierdo = Polinomio.multiplicar(f_nmas2, f_nCubo);
				Polinomio ladoDerecho = Polinomio.multiplicar(Polinomio.multiplicar(FCuadrado, f_nmenos1), f_nmas1Cubo);
				
				resultado = Polinomio.restar(ladoIzquierdo, ladoDerecho);
			}
			
				
		// CASO 2: si k es par (k = 2 * n), entonces f_k = psi_k / psi_2
		} else {
			// n = k / 2
			BigInteger n = k.divide(BigInteger.TWO);
					
			// Polinomios necesarios
			Polinomio f_n = calcularPolinomioDivision(n, modulo, curva);
			Polinomio f_nmenos1 = calcularPolinomioDivision(n.subtract(BigInteger.ONE), modulo, curva);
			Polinomio f_nmas1 = calcularPolinomioDivision(n.add(BigInteger.ONE), modulo, curva);
			Polinomio f_nmenos2 = calcularPolinomioDivision(n.subtract(BigInteger.TWO), modulo, curva);
			Polinomio f_nmas2 = calcularPolinomioDivision(n.add(BigInteger.TWO), modulo, curva);
			
			Polinomio f_nmenos1Cuadrado = Polinomio.multiplicar(f_nmenos1, f_nmenos1);
			Polinomio f_nmas1Cuadrado = Polinomio.multiplicar(f_nmas1, f_nmas1);
						
			
			// En este caso, solo hay una situacion:  f_2n = ( f_n+2 * (f_n-1)^2 - f_n-2 * (f_n+1)^2 ) * f_n
			
			Polinomio ladoIzquierdo = Polinomio.multiplicar(f_nmas2, f_nmenos1Cuadrado);
			Polinomio ladoDerecho = Polinomio.multiplicar(f_nmenos2, f_nmas1Cuadrado);
			Polinomio resta = Polinomio.restar(ladoIzquierdo, ladoDerecho);
			
			resultado = Polinomio.multiplicar(resta, f_n);
		}
		
		resultado.limpiarPolinomio();
		historial.put(k, resultado);
		return resultado;	
	}
	
	public static Polinomio construirF3(BigInteger a, BigInteger b, BigInteger modulo) {
		// f_3 = psi_3 = 3 x^4 + 6 a x^2 + 12 b x - a^2
		
		Polinomio termino1 = Polinomio.crearMonomio(modulo, 4, BigInteger.valueOf(3));
		Polinomio termino2 = Polinomio.crearMonomio(modulo, 2, AritmeticaModular.multiplicar(BigInteger.valueOf(6), a, modulo));
		Polinomio termino3 = Polinomio.crearMonomio(modulo, 1, AritmeticaModular.multiplicar(BigInteger.valueOf(12), b, modulo));
		Polinomio termino4 = Polinomio.crearMonomio(modulo, 0, AritmeticaModular.exponenciar(a, BigInteger.TWO, modulo));
		
		return Polinomio.sumar(Polinomio.sumar(termino1, termino2), Polinomio.restar(termino3, termino4));
	}
	
	public static Polinomio construirF4(BigInteger a, BigInteger b, BigInteger modulo) {
		// f_4 = psi_4 / psi_2 = 4y * (x^6 + 5 a x^4 + 20 b x^3 - 5 a^2 x^2 - 4 a b x - 8 b^2 - a^3) / (2y)
		// f_4 = 2 * (x^6 + 5 a x^4 + 20 b x^3 - 5 a^2 x^2 - 4 a b x - 8 b^2 - a^3)
		
		BigInteger a2 = AritmeticaModular.exponenciar(a, BigInteger.TWO, modulo);
		BigInteger a3 = AritmeticaModular.exponenciar(a, BigInteger.valueOf(3), modulo);
		BigInteger b2 = AritmeticaModular.exponenciar(b, BigInteger.TWO, modulo);
		BigInteger ab = AritmeticaModular.multiplicar(a, b, modulo);
		Polinomio dos = Polinomio.crearMonomio(modulo, 0, BigInteger.TWO);
		
		Polinomio termino1 = Polinomio.crearMonomio(modulo, 6, BigInteger.ONE);
		Polinomio termino2 = Polinomio.crearMonomio(modulo, 4, AritmeticaModular.multiplicar(BigInteger.valueOf(5), a, modulo));
		Polinomio termino3 = Polinomio.crearMonomio(modulo, 3, AritmeticaModular.multiplicar(BigInteger.valueOf(20), b, modulo));
		Polinomio termino4 = Polinomio.crearMonomio(modulo, 2, AritmeticaModular.multiplicar(BigInteger.valueOf(5), a2, modulo));
		Polinomio termino5 = Polinomio.crearMonomio(modulo, 1, AritmeticaModular.multiplicar(BigInteger.valueOf(4), ab, modulo));
		Polinomio termino6 = Polinomio.crearMonomio(modulo, 0, AritmeticaModular.multiplicar(BigInteger.valueOf(8), b2, modulo));
		Polinomio termino7 = Polinomio.crearMonomio(modulo, 0, a3);
		
		Polinomio resultado = Polinomio.sumar(termino1, termino2);
		resultado = Polinomio.sumar(resultado, termino3);
		resultado = Polinomio.restar(resultado, termino4);
		resultado = Polinomio.restar(resultado, termino5);
		resultado = Polinomio.restar(resultado, termino6);
		resultado = Polinomio.restar(resultado, termino7);
		
		return Polinomio.multiplicar(resultado, dos);
	}
}
