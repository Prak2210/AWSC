public enum GeometryAWSC {
	G1(0.2, -0.6, 1.7), G2(0.2, -0.6, 1.7), G3a(0.2, -0.6, 1.7), G3b(0.2, -0.6, 1.7), G4b(0.2, -0.6, 1.7), G5(0.5, -0.7,
			1.7), G6(0.5, -0.7, 1.7);

	private double lt, rt, hv;
	private GeometryAWSC(double lt, double rt, double hv) {
		this.lt = lt;
		this.rt = rt;
		this.hv = hv;
	}

	public double getLt() {
		return lt;
	}

	public double getRt() {
		return rt;
	}

	public double getHv() {
		return hv;
	}

}
