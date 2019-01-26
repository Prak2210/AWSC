public class ThroughAWSCLane extends AWSCLane {

	public ThroughAWSCLane(AWSCBound bound) {
		super(bound);
	}

	// @Override
	// void determineSaturationHeadway() {
	//
	// boolean dedicated = awscBound.isDedicatedRigthTurn();
	// if (!dedicated) {
	// saturationHeadwayDeparture = awscBound.geometryAwsc.getRt() *
	// (getFlowVolume())
	// // TODO: Change sharedRightAndThroughFlowVolume
	// / awscBound.sharedRightAndThroughFlowVolume
	// + awscBound.geometryAwsc.getHv() *
	// awscBound.awsc.percentageHeavyVehicles;
	//
	// } else {
	// saturationHeadwayDeparture = awscBound.geometryAwsc.getHv() *
	// awscBound.awsc.percentageHeavyVehicles;
	//
	// }
	//
	// }

}
