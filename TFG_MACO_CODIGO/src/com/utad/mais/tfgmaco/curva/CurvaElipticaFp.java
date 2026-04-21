package com.utad.mais.tfgmaco.curva;

import java.math.BigInteger;
import com.utad.mais.tfgmaco.punto.*;
import com.utad.mais.tfgmaco.operaciones.*;

public class CurvaElipticaFp extends CurvaEliptica {
	// Atributos
	private BigInteger modulo;
	private PuntoFp puntoInfinito;
	
	// Constructor
	public CurvaElipticaFp(BigInteger a, BigInteger b, BigInteger modulo) {
		super(a, b);
		this.modulo = modulo;
		
		// Para establecer el punto en el infinito, usamos el constructor especial de la clase PuntoFp
		this.puntoInfinito = new PuntoFp(this);
		
		// Ademas, la curva debe ser NO singular
		if(!esCurvaNoSingular()) {
			throw new IllegalArgumentException("La curva es singular: 4 * a^3 + 27 * b^2 ≡ 0 (mod p)");
		}
	}

	// Metodos getter y setter
	@Override
	public BigInteger getModulo() {
		return modulo;
	}

	public void setModulo(BigInteger modulo) {
		this.modulo = modulo;
	}

	@Override
	public PuntoFp getPuntoInfinto() {
		return puntoInfinito;
	}

	public void setPuntoInfinito(PuntoFp puntoInfinito) {
		this.puntoInfinito = puntoInfinito;
	}
	
	// Comprobamos si un punto (x, y) pertenece a la curva
	// Se debe cumplir la igualdad: y^2 = x^3 + ax + b
	@Override
	public boolean perteneceCurva(BigInteger x, BigInteger y) {
		// Calculamos y^2
		BigInteger ladoIzquierdo = AritmeticaModular.exponenciar(y, BigInteger.TWO, modulo);
		
		// Calculamos x^3 + ax + b
		BigInteger xCubo = AritmeticaModular.exponenciar(x, BigInteger.valueOf(3), modulo);
		BigInteger ladoDerecho = AritmeticaModular.sumar(
				(BigInteger)(AritmeticaModular.sumar(xCubo, (BigInteger)(AritmeticaModular.multiplicar(a, x, this.modulo)), this.modulo)), 
				b, modulo);
		
		// Evaluamos la igualdad
		return ladoIzquierdo.equals(ladoDerecho);
	}

	// La curva es NO singular si se cumple que 4 * a^3 + 27 * b^2 !≡ 0 (mod p)
	@Override
	public boolean esCurvaNoSingular() {
		BigInteger aCubo = AritmeticaModular.exponenciar(a, BigInteger.valueOf(3), modulo);
		BigInteger bCuadrado = AritmeticaModular.exponenciar(b, BigInteger.TWO, modulo);

		BigInteger discriminante = AritmeticaModular.sumar(
				(BigInteger)(AritmeticaModular.multiplicar(BigInteger.valueOf(4), aCubo, this.modulo)),
				(BigInteger)(AritmeticaModular.multiplicar(BigInteger.valueOf(27), bCuadrado, this.modulo)),
				modulo);
		
		return !discriminante.equals(BigInteger.ZERO);
	}
}