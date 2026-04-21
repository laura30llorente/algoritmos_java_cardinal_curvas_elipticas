package com.utad.mais.tfgmaco.polinomios;

import java.math.BigInteger;

public class PuntoProyectivoPolinomico {
	// Atributos
	private ElementoExtensionPolinomica X;
	private ElementoExtensionPolinomica Y;
	private ElementoExtensionPolinomica Z;
	private boolean puntoInfinito;
	
	// Constructor para un punto normal
	// P = (A_x + B_x * y, A_y + B_y * y, A_z + B_z * y)
	public PuntoProyectivoPolinomico(ElementoExtensionPolinomica X, ElementoExtensionPolinomica Y, ElementoExtensionPolinomica Z) {
		this.X = X;
		this.Y = Y;
		this.Z = Z;
		this.puntoInfinito = false;
	}
	
	// Constructor para el punto en el infinito (0:1:0)
	public PuntoProyectivoPolinomico(BigInteger modulo, Polinomio polinomioCurva, Polinomio polinomioDiv) {
		this.X = ElementoExtensionPolinomica.elementoCero(modulo, polinomioCurva, polinomioDiv);
		this.Y = ElementoExtensionPolinomica.elementoUno(modulo, polinomioCurva, polinomioDiv);
		this.Z = ElementoExtensionPolinomica.elementoCero(modulo, polinomioCurva, polinomioDiv);
		this.puntoInfinito = true;
	}

	// Metodos getters y setters
	
	public ElementoExtensionPolinomica getX() {
		return X;
	}

	public void setX(ElementoExtensionPolinomica x) {
		X = x;
	}

	public ElementoExtensionPolinomica getY() {
		return Y;
	}

	public void setY(ElementoExtensionPolinomica y) {
		Y = y;
	}

	public ElementoExtensionPolinomica getZ() {
		return Z;
	}

	public void setZ(ElementoExtensionPolinomica z) {
		Z = z;
	}

	public boolean esPuntoInfinito() {
		return puntoInfinito;
	}

	public void setPuntoInfinito(boolean puntoInfinito) {
		this.puntoInfinito = puntoInfinito;
	}
}