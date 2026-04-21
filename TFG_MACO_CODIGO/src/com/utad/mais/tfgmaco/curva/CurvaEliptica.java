package com.utad.mais.tfgmaco.curva;

import java.math.BigInteger;
import com.utad.mais.tfgmaco.punto.*;

public abstract class CurvaEliptica {
	// Atributos de una curva eliptica: y^2 = x^3 + ax + b
	protected BigInteger a;
	protected BigInteger b;
	
	// Constructor
	public CurvaEliptica(BigInteger a, BigInteger b) {
		this.a = a;
		this.b = b;
	}
	
	// Metodos getter para obtener los coeficientes de la curva
	public BigInteger getA() {
		return a;
	};
	public BigInteger getB() {
		return b;
	};
	
	// Otros metodos
	public abstract BigInteger getModulo();
	public abstract Punto getPuntoInfinto();
	public abstract boolean perteneceCurva(BigInteger x, BigInteger y);
	public abstract boolean esCurvaNoSingular();
}
