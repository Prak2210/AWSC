public interface AWSCBoundComputationEngine {

	// Step 1: Convert Movement Demand Volumes to Flow Rates
	public void convertVolumeDemandVolumesToFlowRates();

	// Step 2: Determine Lane Flow Rates
	public void determineLaneFlowRates();

	// Step 4: Determine Saturation Headway Adjustments
	public void determineSaturationHeadwayAdjustments();

	// Step 6: Calculate Initial Degree of Utilization
	public void calculateInitialDegreeOfUtilization();

	// Step 7: Compute Probability States
	public void computeProbabilityStates();

	// Step 8: Compute Probability Adjustment Factors
	public void computeProbabilityAdjustmentFactors();

	// Step 9: Compute Saturation Headways
	public void computeSaturationHeadways();

	// Step 10: Compute Departure Headways
	public void computeDepartureHeadways();

	// Step 11: Check for Convergence
	public void checkForConvergence();

	// Step 12: Compute Capacity
	public void computeCapacity();

	// Step 13: Compute Service Times
	public void computeServiceTimes();

	// Step 14: Compute Control Delay and Determine LOS for Each Lane
	public void computeControlDelayLane();

	// Step 15: Compute Control Delay and Determine LOS for Each Approach
	public void computeControlDelayApproach();

	// Step 16: Compute Queue Lengths
	public void computeQueueLength();
}
