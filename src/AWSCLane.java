public class AWSCLane extends Lane {

	double moveUpTime = 2.3;

	private double flowVolume;
	protected double saturationHeadwayDeparture;
	AWSCBound awscBound;
	private double intialHeadwayDeparture = 3.2;
	double[][] degreeOfUtilization = new double[3][];
	double degreeUtilization;
	private double previousInitialHeadwayDeparture = 3.2;
	double[][] degreeOfConflictCase = AWSCInternalConfigurations.degreeOfConflictCase;
	public double leftVolume,volume;
	public double rightVolume,throughVolume;
	public double flowLeftVolume, flowRightVolume, flowThroughVolume;
	static public double phf;
	public double lanePercentile;

	double[] docProbSum = new double[6];
	//
	double[][] probabilityStates = new double[3][];
	private double alpha = 0.01;
	private double[] docProbAjdust = new double[6];
	double[] saturationHeadways = new double[512];
	private double[] probabilityStatesDoc = new double[512];
	private double diff = 1;
	static public double percentageHeavyVehicles;

	double serviceTime;

	double finalHeadway;

	double delay;

	public AWSCLane(AWSCBound bounds) {
		this.awscBound = bounds;
	}

	/* step 1 */
	public double convertVolumeDemandVolumesToFlowRates(){
		this.flowVolume = volume / phf;
		flowLeftVolume = leftVolume / phf;
		flowRightVolume = rightVolume / phf;
		flowThroughVolume = throughVolume / phf;
		//System.out.println(flowLeftVolume+" "+flowThroughVolume+" "+flowRightVolume+" "+flowVolume);
		//System.out.println(leftVolume+" "+throughVolume+" "+rightVolume);
		return flowVolume;
	}
	
	//step 4
	void determineSaturationHeadway() {
		
		if(flowVolume!=0)
		{
		saturationHeadwayDeparture = awscBound.geometryAwsc.getLt() * (flowLeftVolume / flowVolume)
				+ awscBound.geometryAwsc.getRt() * (flowRightVolume / flowVolume)
				+ awscBound.geometryAwsc.getHv() * percentageHeavyVehicles;
		//System.out.println(saturationHeadwayDeparture);
		}
		
	}
	
	//step 6
	public void computeDegreeOfUtilizationBound() {
		double[][] volumes = awscBound.computeVolumeForOtherLanes();
		double[][] otherHeadwayDeparture = awscBound.getHeadwayDepartureForOtherBounds();
		for (int i = 0; i < volumes.length; i++) {
			degreeOfUtilization[i] = new double[volumes[i].length];
			for (int j = 0; j < volumes[i].length; j++) {
				degreeOfUtilization[i][j] = computeDegreeOfUtilizationLane(volumes[i][j], otherHeadwayDeparture[i][j]);
				//System.out.println(degreeOfUtilization[i][j]);
			}
		}
	}
	
	//step 6
	private double computeDegreeOfUtilizationLane(double otherBoundFlowVolume, double otherHeadwayDeparture) {
		double degreeofUtilization;
		degreeUtilization=degreeofUtilization= otherBoundFlowVolume * otherHeadwayDeparture / 3600;
		if (degreeofUtilization > 1) {
			degreeofUtilization = 1;
		}
		//System.out.println(degreeofUtilization);
		return degreeofUtilization;
	}


	public void computeProbabilityStates() {
		double[][] otherVolume = awscBound.computeVolumeForOtherLanes();
		probabilityStates[0] = new double[3];
		probabilityStates[1] = new double[3];
		probabilityStates[2] = new double[3];
		for (int i = 0; i < degreeOfConflictCase.length; i++) {
			probabilityStates[0] = new double[3];
			probabilityStates[1] = new double[3];
			probabilityStates[2] = new double[3];
			double probabilityRow = 1d;
			// 10 to account for storing the probability
			for (int j = 2; j < 11; j++) {
				int row = (j - 2) / 3;
				int column = (j - 2) % 3;
				if (column >= degreeOfUtilization[row].length)
					continue;

				if (otherVolume[row][column] > 0) {
					if (degreeOfConflictCase[i][j] == 0) {
						probabilityStates[row][column] = 1 - degreeOfUtilization[row][column];
					} else {
						probabilityStates[row][column] = degreeOfUtilization[row][column];
					}
				} else {
					if (degreeOfConflictCase[i][j] == 0) {
						probabilityStates[row][column] = 1;
					} else {
						probabilityStates[row][column] = 0;
					}

				}
				 //Test.print(probabilityStates[row][column] + " ", true);
				probabilityRow *= probabilityStates[row][column];
				//System.out.println(probabilityRow);
			}
			// Test.print("");
			probabilityStatesDoc[i] = (probabilityRow);
		}
		 //Test.printArr(probabilityStatesDoc);
	}

	public void computeProbabilityAdjustmentFactors() {

		docProbSum = new double[6];
		for (int i = 0; i < degreeOfConflictCase.length; i++) {
			int x = (int) degreeOfConflictCase[i][0];
			docProbSum[x] += (probabilityStatesDoc[i]);
		}

		adjsutProbabilityDoc();
		//Test.printArr(docProbSum);
		for (int i = 0; i < degreeOfConflictCase.length; i++) {
			probabilityStatesDoc[i] += (docProbAjdust[(int) degreeOfConflictCase[i][0]]);
		}
	}

	private void adjsutProbabilityDoc() {
		docProbAjdust[1] = alpha * (docProbSum[2] + docProbSum[3] * 2 + docProbSum[4] * 3 + docProbSum[5] * 4);
		docProbAjdust[2] = alpha * (docProbSum[3] + docProbSum[4] * 2 + docProbSum[5] * 3 - docProbSum[2]) / 7;
		docProbAjdust[3] = alpha * (docProbSum[4] + docProbSum[5] * 2 - docProbSum[3] * 3) / 14;
		docProbAjdust[4] = alpha * (docProbSum[5] - 6 * docProbSum[4]) / 147;
		docProbAjdust[5] = -alpha * (10 * docProbSum[5]) / 343;
	}

	public void computeSaturationHeadways() {
		for (int i = 0; i < degreeOfConflictCase.length; i++) {
			double computeBaseSaturationHeadway = AWSCInternalConfigurations.computeBaseSaturationHeadway(
					degreeOfConflictCase[i][1], degreeOfConflictCase[i][0], awscBound.geometryAwsc);
			if (computeBaseSaturationHeadway == 0)
				continue;
			saturationHeadways[i] = (saturationHeadwayDeparture + computeBaseSaturationHeadway);
			//System.out.println(saturationHeadways[i]);
		}
		// Test.debugF();
		// Test.printArr(saturationHeadways);
	}

	public void computeDepartureHeadways() {
		previousInitialHeadwayDeparture = intialHeadwayDeparture;
		intialHeadwayDeparture = 0;
		for (int i = 0; i < probabilityStatesDoc.length; i++) {
			if (saturationHeadways[i] == 0)
				continue;
			intialHeadwayDeparture = (intialHeadwayDeparture + probabilityStatesDoc[i] * saturationHeadways[i]);
		}
		diff = (intialHeadwayDeparture - previousInitialHeadwayDeparture);
		//System.out.println(intialHeadwayDeparture);
	}

	/**
	 * Step 13
	 */
	void computeServiceTime() {
		serviceTime = intialHeadwayDeparture - moveUpTime;
		//System.out.println(serviceTime+" "+intialHeadwayDeparture+" "+moveUpTime);
	}

	public void degreeOfUtilization() {
		finalHeadway = (flowVolume * previousInitialHeadwayDeparture) / 3600;
	}

	/**
	 * Step 14
	 * 
	 */
	public void computeControlDelayAndLOS() {
		if(flowVolume!=0)
		{
			double z = Math.round(serviceTime * 100) / 100d;
			double a = Math.round((finalHeadway - 1) * 100d) / 100d;
			double b = ((finalHeadway * intialHeadwayDeparture) / (450d * 0.25d));
			double c = Math.pow(a, 2) + b;
			delay = z + (900 * 0.25 * (a + (Math.sqrt(c)))) + 5d;
			
			//step 16
			b = ((finalHeadway * intialHeadwayDeparture) / (150d * 0.25d));
			c = Math.pow(a, 2) + b;
			lanePercentile = Math.ceil((900 * 0.25 * (a + (Math.sqrt(c))))/this.intialHeadwayDeparture);
			//System.out.println(lanePercentile);
		}	
	}
	

	public double getDiff() {
		return diff;
	}

	public void setDiff(double diff) {
		this.diff = diff;
	}

	public double getIntialHeadwayDeparture() {
		return intialHeadwayDeparture;
	}

	public void setIntialHeadwayDeparture(double intialHeadwayDeparture) {
		this.intialHeadwayDeparture = intialHeadwayDeparture;
	}

	public double getPreviousInitialHeadwayDeparture() {
		return previousInitialHeadwayDeparture;
	}

	public void setPreviousInitialHeadwayDeparture(double previousInitialHeadwayDeparture) {
		this.previousInitialHeadwayDeparture = previousInitialHeadwayDeparture;
	}

	@Override
	public String toString() {
		return super.toString();
	}

	public double getFinalHeadway() {
		return finalHeadway;
	}

	public void setFinalHeadway(double finalHeadway) {
		this.finalHeadway = finalHeadway;
	}

	public double getLeftVolume() {
		return leftVolume;
	}

	public void setLeftVolume(double leftVolume) {
		this.leftVolume = leftVolume;
	}

	public double getRightVolume() {
		return rightVolume;
	}

	public void setRightVolume(double rightVolume) {
		this.rightVolume = rightVolume;
	}

	public double getThroughVolume() {
		return throughVolume;
	}

	public void setThroughVolume(double throughVolume) {
		this.throughVolume = throughVolume;
	}

	public double getFlowLeftVolume() {
		return flowLeftVolume;
	}

	public void setFlowLeftVolume(double flowLeftVolume) {
		this.flowLeftVolume = flowLeftVolume;
	}

	public double getFlowRightVolume() {
		return flowRightVolume;
	}

	public void setFlowRightVolume(double flowRightVolume) {
		this.flowRightVolume = flowRightVolume;
	}

	public double getFlowThroughVolume() {
		return flowThroughVolume;
	}

	public void setFlowThroughVolume(double flowThroughVolume) {
		this.flowThroughVolume = flowThroughVolume;
	}


	public void setFlowVolume(double flowVolume) {
		this.flowVolume = flowVolume;
	}

	public double getVolume() {
		return volume;
	}

	public void setVolume(double volume) {
		this.volume = volume;
	}

	public double getFlowVolume() {
		return flowVolume;
	}

}
