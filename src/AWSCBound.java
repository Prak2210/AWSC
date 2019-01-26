public abstract class AWSCBound {

	// Constants
	protected AWSC awsc;
	AWSCLane lane1;
	AWSCLane lane2;
	AWSCLane lane3;

	static public double intersectionDelay;
	protected AWSCBound leftAwscBound;
	protected AWSCBound rightAwscBound;
	protected AWSCBound oppositeAwscBound;
	public double leftLaneFlowRate, rightLaneFlowRate, throughLaneFlowRate;
	
	public AWSCBound(AWSC awsc2, int noOfLanes, double[] sv) {
		this.noOfLanes = noOfLanes;
		if (noOfLanes >= 1) 
		{
			lane1 = new AWSCLane(this);
			lane1.setLeftVolume(sv[0]);
			lane1.setVolume(sv[0]);
			if (noOfLanes == 1) 
			{
				lane1.setThroughVolume(sv[1]);
				lane1.setRightVolume(sv[2]);
				lane1.setVolume(sv[0] + sv[1] + sv[2]);
			} 
			else if (noOfLanes == 2)
			{
				lane2 = new AWSCLane(this);
				lane2.setThroughVolume(sv[1]);
				lane2.setRightVolume(sv[2]);
				lane2.setVolume(sv[1] + sv[2]);
				
			}
			if (noOfLanes == 3) 
			{
				lane2 = new AWSCLane(this);
				lane2.setThroughVolume(sv[1]);
				lane2.setVolume(sv[1]);
				
				lane3 = new AWSCLane(this);
				lane3.setRightVolume(sv[2]);
				lane3.setVolume(sv[2]);
			}
		}
		leftAwscBound =this.getLeftAwscBound();
		rightAwscBound = this.getRightAwscBound();
		oppositeAwscBound = this.getOppositeAwscApproach();
		
	}


	// Vi =demand volume for movement i (veh/h), and
	private double[][] otherVolume = new double[3][3];

	int noOfLanes;

	/**
	 * 
	 */
	// double sharedRightAndThroughFlowVolume;
	GeometryAWSC geometryAwsc;
	private boolean converged = true;
	private double[][] otherHeadwayDeparture = new double[3][3];
	double totalDelay;

	// Step 5
	// Step 6

	void step1() {
		Test.bound = this;
		lane1.convertVolumeDemandVolumesToFlowRates();
		if (lane2 != null)
			lane2.convertVolumeDemandVolumesToFlowRates();
		if (lane3 != null)
			lane3.convertVolumeDemandVolumesToFlowRates();
		this.totalBoundVolume();
	}
	
	void step2(){
		//lane flow rates 
		if(this.noOfLanes==2)
		{
			this.leftLaneFlowRate = lane1.flowLeftVolume +lane2.flowLeftVolume;
			this.rightLaneFlowRate=this.throughLaneFlowRate = lane1.flowThroughVolume + lane2.flowThroughVolume +lane1.flowRightVolume + lane2.flowRightVolume;
		}
		else if(this.noOfLanes>2)
		{
			this.leftLaneFlowRate = lane1.flowLeftVolume +lane2.flowLeftVolume +lane3.flowLeftVolume;
			this.throughLaneFlowRate = lane1.flowThroughVolume + lane2.flowThroughVolume + lane3.flowThroughVolume;
			this.rightLaneFlowRate = lane1.flowRightVolume + lane2.flowRightVolume + lane3.flowRightVolume;
		}
	}
	
	 void step3() {
		determineGeometry();
	}
	
	 void step4(){	
		 lane1.determineSaturationHeadway();
			if (lane2 != null)
				lane2.determineSaturationHeadway();
			if (lane3 != null)
				lane3.determineSaturationHeadway();
	}

	void degreeOfUtilize() {
		Test.bound = this;
		Test.lane = lane1;
		lane1.computeDegreeOfUtilizationBound();
		Test.lane = lane2;
		if (lane2 != null)
			lane2.computeDegreeOfUtilizationBound();
		if (lane3 != null) {
			Test.lane = lane3;
			lane3.computeDegreeOfUtilizationBound();
		}
	}

	boolean compute() {
		lane1.computeProbabilityStates();
		lane1.computeProbabilityAdjustmentFactors();
		lane1.computeSaturationHeadways();
		lane1.computeDepartureHeadways();
		converged = true;
		converged = converged && lane1.getDiff() <= 0.1;

		if (lane3 != null) {
			lane3.computeProbabilityStates();
			lane3.computeProbabilityAdjustmentFactors();
			lane3.computeSaturationHeadways();
			lane3.computeDepartureHeadways();
			converged = converged && lane3.getDiff() <= 0.1;
		}

		if (lane2 != null) {
			lane2.computeProbabilityStates();
			lane2.computeProbabilityAdjustmentFactors();
			lane2.computeSaturationHeadways();
			lane2.computeDepartureHeadways();
			converged = converged && lane2.getDiff() <= 0.1;
		}

		return converged;
	}

	void finalSteps() {
		lane1.computeServiceTime();
		
		lane1.degreeOfUtilization();
		lane1.computeControlDelayAndLOS();

		if (lane3 != null) {
			lane3.computeServiceTime();
			lane3.degreeOfUtilization();
			lane3.computeControlDelayAndLOS();
		}
		if (lane2 != null) {
			lane2.computeServiceTime();
			lane2.degreeOfUtilization();
			lane2.computeControlDelayAndLOS();
		}
		computeDelay();
	}

	public void computeDelay() {
		if(this.totalBoundVolume()!=0)
		{
			totalDelay = ((lane1 != null ? lane1.getFlowVolume() * lane1.delay : 0)
					+ (lane2 != null ? lane2.getFlowVolume() * lane2.delay : 0)
					+ (lane3 != null ? lane3.getFlowVolume() * lane3.delay : 0)) / totalBoundVolume();
			//System.out.println(totalDelay);
		}
	}

	private double totalBoundVolume() {
		double totalVolume = 0.0d;
		if (lane1 != null)
			totalVolume += lane1.getFlowVolume();
		if (lane2 != null)
			totalVolume += lane2.getFlowVolume();
		if (lane3 != null)
			totalVolume += lane3.getFlowVolume();
		//System.out.println(totalVolume);
		totalVolume = Math.round(totalVolume);
		return totalVolume;
	}
	
	static void computeFinalDelay(AWSCBound eastBound,AWSCBound westBound, AWSCBound northBound, AWSCBound southBound) {
		 intersectionDelay = ((northBound.totalDelay * northBound.totalVolume())
				 			+ (southBound.totalDelay * southBound.totalVolume()) +
				 			(eastBound.totalDelay * eastBound.totalVolume())
				 			+ (westBound.totalDelay * westBound.totalVolume()))
				 			/ (northBound.totalVolume() + southBound.totalVolume() +
				 			  eastBound.totalVolume()+ westBound.totalVolume());
		 intersectionDelay = Math.ceil(intersectionDelay);
		 // System.out.println(intersectionDelay);
		 }
	
	public double totalVolume() {
		return totalBoundVolume();
	}

	public void computeServiceTimes() {

	}

	public void computeControlDelayLane() {
	}

	public void computeControlDelayApproach() {
	}

	public void computeQueueLength() {
	}

	public AWSCBound getOppositeAwscBound() {
		return oppositeAwscBound;
	}

	public void setOppositeAwscBound(AWSCBound oppositeAwscBound) {
		this.oppositeAwscBound = oppositeAwscBound;
	}

	public AWSCBound getLeftAwscBound() {
		return leftAwscBound;
	}

	public void setLeftAwscBound(AWSCBound leftAwscBound) {
		this.leftAwscBound = leftAwscBound;
	}

	public AWSCBound getRightAwscBound() {
		return rightAwscBound;
	}

	public void setRightAwscBound(AWSCBound rightAwscBound) {
		this.rightAwscBound = rightAwscBound;
	}

	public AWSCBound getOppositeAwscApproach() {
		return oppositeAwscBound;
	}

	public void setOppositeAwscApproach(AWSCBound oppositeAwscApproach) {
		this.oppositeAwscBound = oppositeAwscApproach;
	}

	public int getNoOfLanes() {
		return noOfLanes;
	}

	public void setNoOfLanes(int noOfLanes) {
		this.noOfLanes = noOfLanes;
	}

	public GeometryAWSC getGeometry() {
		return geometryAwsc;
	}

	public AWSCLane getLane1() {
		return lane1;
	}

	public AWSCLane getLane2() {
		return lane2;
	}

	public AWSCLane getLane3() {
		return lane3;
	}

	public void setLane1(AWSCLane lane) {
		this.lane1 = lane;
	}

	public void setLane2(AWSCLane lane) {
		this.lane2 = lane;
	}

	public void setLane3(AWSCLane lane) {
		this.lane3 = lane;
	}

	public double[][] computeVolumeForOtherLanes() {
		otherVolume[0] = oppositeAwscBound.getVolumes();
		otherVolume[1] = leftAwscBound.getVolumes();
		otherVolume[2] = rightAwscBound.getVolumes();
		return otherVolume;
	}

	public double[][] getHeadwayDepartureForOtherBounds() {
		otherHeadwayDeparture[0] = oppositeAwscBound.getHeadwayDeparture();
		otherHeadwayDeparture[1] = leftAwscBound.getHeadwayDeparture();
		otherHeadwayDeparture[2] = rightAwscBound.getHeadwayDeparture();
		return otherHeadwayDeparture;

	}

	public double[] getHeadwayDeparture() {
		double[] vol = new double[3];
		vol[0] = lane1.getIntialHeadwayDeparture();
		if (lane2 != null)
			vol[1] = lane2.getIntialHeadwayDeparture();
		if (lane3 != null)
			vol[2] = lane3.getIntialHeadwayDeparture();
		return vol;
	}

	public double[] getPreviousHeadwayDeparture() {
		double[] vol = new double[3];
		vol[0] = lane1.getPreviousInitialHeadwayDeparture();
		if (lane2 != null)
			vol[1] = lane2.getPreviousInitialHeadwayDeparture();
		if (lane3 != null)
			vol[2] = lane3.getPreviousInitialHeadwayDeparture();
		return vol;
	}

	public double[] getVolumes() {
		double[] vol = new double[3];
		vol[0] = lane1.convertVolumeDemandVolumesToFlowRates();
		if (lane3 != null)
			vol[2] = lane3.convertVolumeDemandVolumesToFlowRates();
		if (lane2 != null)
			vol[1] = lane2.convertVolumeDemandVolumesToFlowRates();
		return vol;
	}
	
	public void determineGeometry() {
		int oppositNoOfLanes = oppositeAwscBound.getNoOfLanes();
		int conflictingApproachNoOfLanes = Math.max(leftAwscBound.getNoOfLanes(), rightAwscBound.getNoOfLanes());
		int noOfLanes = getNoOfLanes();
		if (noOfLanes == 1) {
			if (oppositNoOfLanes == 0 || oppositNoOfLanes == 1) {
				if (conflictingApproachNoOfLanes == 1) {
					geometryAwsc = GeometryAWSC.G1;
				} else if (conflictingApproachNoOfLanes == 2) {
					geometryAwsc = GeometryAWSC.G2;
				} else if (conflictingApproachNoOfLanes == 3) {
					geometryAwsc = GeometryAWSC.G5;
				}
			} else if (oppositNoOfLanes == 2) {
				if (conflictingApproachNoOfLanes == 1) {
					geometryAwsc = GeometryAWSC.G3a;
				} else if (conflictingApproachNoOfLanes == 2) {
					if (awsc.noOfBounds == 4) {
						geometryAwsc = GeometryAWSC.G3b;
					} else {
						geometryAwsc = GeometryAWSC.G4b;
					}
				} else if (conflictingApproachNoOfLanes == 2) {
					geometryAwsc = GeometryAWSC.G6;
				}
			} else if (oppositNoOfLanes == 3) {
				if (conflictingApproachNoOfLanes == 1) {
					geometryAwsc = GeometryAWSC.G5;
				} else if (conflictingApproachNoOfLanes == 2 || conflictingApproachNoOfLanes == 3) {
					geometryAwsc = GeometryAWSC.G6;
				}
			}
		} else if (noOfLanes == 2) {
			if (oppositNoOfLanes == 0 || oppositNoOfLanes == 1 || oppositNoOfLanes == 2) {
				if (conflictingApproachNoOfLanes == 1 || conflictingApproachNoOfLanes == 2) {
					geometryAwsc = GeometryAWSC.G5;
				} else if (conflictingApproachNoOfLanes == 3) {
					geometryAwsc = GeometryAWSC.G6;
				}
			} else if (oppositNoOfLanes == 3) {
				geometryAwsc = GeometryAWSC.G6;
			}
		} else if (noOfLanes == 3) {
			if (oppositNoOfLanes == 0 || oppositNoOfLanes == 1) {
				geometryAwsc = GeometryAWSC.G5;
			} else if (oppositNoOfLanes == 2 || oppositNoOfLanes == 3) {
				if (conflictingApproachNoOfLanes == 1) {
					geometryAwsc = GeometryAWSC.G5;
				} else if (conflictingApproachNoOfLanes == 2 || conflictingApproachNoOfLanes == 3) {
					geometryAwsc = GeometryAWSC.G6;
				}
			}
		} else {
			System.err.println("Error while determining the geometry!");
		}
		//System.out.println(geometryAwsc);
	}

}
