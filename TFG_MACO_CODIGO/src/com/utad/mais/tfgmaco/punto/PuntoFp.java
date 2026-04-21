package com.utad.mais.tfgmaco.punto;

import java.math.BigInteger;
import com.utad.mais.tfgmaco.curva.*;

public class PuntoFp extends Punto {
	// Atributos
	private BigInteger x;
	private BigInteger y;
	private boolean puntoInfinito;
	
	// Constructor para un punto normal
	public PuntoFp(BigInteger x, BigInteger y, CurvaElipticaFp curva) {
		super(curva);
		this.x = x;
		this.y = y;
		this.puntoInfinito = false;
	}
	
	// Constructor para el punto en el infinito -> no tiene coordenadas en el plano afin
	public PuntoFp(CurvaElipticaFp curva) {
		super(curva);
		this.x = null;
		this.y = null;
		this.puntoInfinito = true;
	}

	// Metodos getter y setter
	@Override
	public BigInteger getX() {
		return x;
	}

	public void setX(BigInteger x) {
		this.x = x;
	}

	@Override
	public BigInteger getY() {
		return y;
	}

	public void setY(BigInteger y) {
		this.y = y;
	}

	@Override
	public boolean esPuntoInfinito() {
		return puntoInfinito;
	}

	public void setPuntoInfinito(boolean puntoInfinito) {
		this.puntoInfinito = puntoInfinito;
	}
}