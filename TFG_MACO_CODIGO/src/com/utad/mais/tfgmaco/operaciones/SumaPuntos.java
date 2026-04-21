package com.utad.mais.tfgmaco.operaciones;

import java.math.BigInteger;
import com.utad.mais.tfgmaco.curva.*;
import com.utad.mais.tfgmaco.punto.*;

public class SumaPuntos {
	
	// Metodo para elegir el caso de suma de puntos
	public static PuntoFp sumarPuntos(PuntoFp P, PuntoFp Q) {
		
		// Comprobamos que los puntos no son null y estan en la misma curva
		if (P == null || Q == null || !P.getCurva().equals(Q.getCurva())) {
			throw new IllegalArgumentException("Puntos no validos o pertenecientes a curvas distintas");
		}
		
		// Por la propiedad algebraica de elemento neutro: P + PuntoInfinito = P
		if (P.esPuntoInfinito()) {
			return Q;
		}
		if (Q.esPuntoInfinito()) {
			return P;
		}
		
		// Obtenemos las coordenadas de los puntos
		BigInteger x1 = P.getX();
		BigInteger y1 = P.getY();
		BigInteger x2 = Q.getX();
		BigInteger y2 = Q.getY();
		
		BigInteger modulo = P.getCurva().getModulo();
		BigInteger a = P.getCurva().getA();
		
		// Caso 1: Puntos distintos -> x1 != x2
		if(!x1.equals(x2)) {
			
			return sumaPuntosDistintos(P.getCurva(), x1, y1, x2, y2, modulo);
		
		} else {
			
			// Caso 3: Puntos opuestos -> y1 = -y2
			if (y1.equals(y2.negate().mod(modulo))) {
				
				// En este caso, ocurre que Q = -P, por lo que P + (-P) = PuntoInfinito
				return P.getCurva().getPuntoInfinto();
				
			} else {   // Caso 2: Puntos coincidentes (duplicacion) -> P = Q
				
				return sumaPuntosDuplicacion(P.getCurva(), x1, y1, a, modulo);
			}
		}
	}
	
	// CASO 1: PUNTOS DISTINTOS
	public static PuntoFp sumaPuntosDistintos(CurvaElipticaFp curva, BigInteger x1, BigInteger y1, BigInteger x2, BigInteger y2, BigInteger modulo) {
		// Pendiente: m = (y2 - y1) / (x2 - x1) mod modulo
		
		BigInteger numerador = AritmeticaModular.restar(y2, y1, modulo);
		BigInteger denominador = AritmeticaModular.restar(x2, x1, modulo);
		BigInteger m = AritmeticaModular.division(numerador, denominador, modulo);
		
		// Coordenada x3 = m^2 - x1 - x2
		
		BigInteger mCuadrado = AritmeticaModular.exponenciar(m, BigInteger.TWO, modulo);
		BigInteger x3 = AritmeticaModular.restar((BigInteger)(AritmeticaModular.restar(mCuadrado, x1, modulo)), x2, modulo);
		
		// Coordenada y3 = m * (x1 - x3) - y1
		
		BigInteger producto = AritmeticaModular.multiplicar(m, (BigInteger)(AritmeticaModular.restar(x1, x3, modulo)), modulo);
		BigInteger y3 = AritmeticaModular.restar(producto, y1, modulo);
		
		// Devolvemos el punto P + Q = (x3, y3)
		return new PuntoFp(x3, y3, curva);
	}
	
	// CASO 2: PUNTOS COINCIDENTES
	public static PuntoFp sumaPuntosDuplicacion(CurvaElipticaFp curva, BigInteger x1, BigInteger y1, BigInteger a, BigInteger modulo) {
		// Pendiente: m = (3 * x1^2 + a) / (2 * y1) mod modulo
		
		BigInteger x1Cuadrado = AritmeticaModular.exponenciar(x1, BigInteger.TWO, modulo);
		BigInteger numerador = AritmeticaModular.sumar(
											(BigInteger)(AritmeticaModular.multiplicar(BigInteger.valueOf(3), x1Cuadrado, modulo)),
											a, modulo);
		BigInteger denominador = AritmeticaModular.multiplicar(BigInteger.valueOf(2), y1, modulo);
		BigInteger m = AritmeticaModular.division(numerador, denominador, modulo);
		
		// Coordenada x3 = m^2 - 2 * x1
		
		BigInteger mCuadrado = AritmeticaModular.exponenciar(m, BigInteger.TWO, modulo);
		BigInteger x3 = AritmeticaModular.restar(mCuadrado, (BigInteger)(AritmeticaModular.multiplicar(BigInteger.TWO, x1, modulo)), modulo);
		
		// Coordenada y3 = m * (x1 - x3) - y1
		
		BigInteger producto = AritmeticaModular.multiplicar(m, (BigInteger)(AritmeticaModular.restar(x1, x3, modulo)), modulo);
		BigInteger y3 = AritmeticaModular.restar(producto, y1, modulo);
		
		// Devolvemos el punto 2P = (x3, y3)
		return new PuntoFp(x3, y3, curva);
	}
}