import java.util.ArrayList;

public class AWSC {
	AWSCBound northBound, southBound, eastBound, westBound;
	public int noOfBounds = 4;
	int counter;
	boolean debug=false;

	public AWSC(double[] ds, int[] awscLaneCount) {
		init(ds, awscLaneCount);
	}

	private void init(double[] ds, int[] awscLaneCount) {
		
		// {left, through, right}
		double[] ev = { ds[0], ds[1], ds[2] };
		double[] wv = { ds[3], ds[4], ds[5] };
		double[] nv = { ds[6], ds[7], ds[8] };
		double[] sv = { ds[9], ds[10], ds[11] };
		eastBound = new EastAWSCBound(this, awscLaneCount[0], ev);
		westBound = new WestAWSCBound(this, awscLaneCount[1], wv);
		northBound = new NorthAWSCBound(this, awscLaneCount[2], nv);
		southBound = new SouthAWSCBound(this, awscLaneCount[3], sv);
		this.noOfBounds = (int) ds[12];
		AWSCLane.phf = ds[13];
		AWSCLane.percentageHeavyVehicles = ds[14];
		
		eastBound.setOppositeAwscBound(westBound);
		eastBound.setLeftAwscBound(southBound);
		eastBound.setRightAwscBound(northBound);
		
		westBound.setOppositeAwscBound(eastBound);
		westBound.setLeftAwscBound(northBound);
		westBound.setRightAwscBound(southBound);

		northBound.setOppositeAwscBound(southBound);
		northBound.setLeftAwscBound(eastBound);
		northBound.setRightAwscBound(westBound);
		
		southBound.setOppositeAwscBound(northBound);
		southBound.setLeftAwscBound(westBound);
		southBound.setRightAwscBound(eastBound);
	}

	public ArrayList<AWSCBound> computeSteps() {
		
		ArrayList<AWSCBound> output = new ArrayList<>();

		//step 1
		eastBound.step1();
		westBound.step1();
		northBound.step1();
		southBound.step1();
		
		//step 2
		eastBound.step2();
		westBound.step2();
		northBound.step2();
		southBound.step2();
		
		if(debug)
		{
			System.out.println("-----------Step 1 & 2------------");
			System.out.println("Veb,l :"+eastBound.leftLaneFlowRate);
			if(eastBound.lane2!=null)
				{
				System.out.println("Veb,t :"+eastBound.lane2.flowThroughVolume);
				System.out.println("Veb,r : "+eastBound.lane2.flowRightVolume);
				}
			if(eastBound.lane3!=null)
			System.out.println("Veb,r : "+eastBound.lane3.flowRightVolume);
			System.out.println();
			System.out.println("Vwb,l :"+westBound.leftLaneFlowRate);
			System.out.println("Vwb,t :"+westBound.throughLaneFlowRate);
			System.out.println("Vwb,r : "+westBound.rightLaneFlowRate);
			System.out.println();
			System.out.println("Vnb,l :"+northBound.leftLaneFlowRate);
			System.out.println("Vnb,t :"+northBound.throughLaneFlowRate);
			System.out.println("Vnb,r : "+northBound.rightLaneFlowRate);
			System.out.println();
			System.out.println("Vsb,l :"+southBound.leftLaneFlowRate);
			System.out.println("Vsb,t :"+southBound.throughLaneFlowRate);
			System.out.println("Vsb,r : "+southBound.rightLaneFlowRate);
			System.out.println("-------End of Step 1 & 2---------"+'\n');
			
			System.out.println("-----------TOTAL VOLUME of APPROACH------------");
			System.out.println("Veb :"+eastBound.totalVolume());
			System.out.println("Vwb :"+westBound.totalVolume());
			System.out.println("Vnb :"+northBound.totalVolume());
			System.out.println("Vsb :"+southBound.totalVolume());
			System.out.println("-----------------------------------------------"+'\n');
			
		}
		
		//step 3
		eastBound.step3();
		westBound.step3();
		northBound.step3();
		southBound.step3();
		
		if(debug)
		{
			System.out.println("-----------Step 3------------");
			System.out.println("Geometry grp of intersaction :"+eastBound.geometryAwsc);
			System.out.println("-------End of Step 3---------"+'\n');
		}
			
		//step 4
		eastBound.step4();
		westBound.step4();
		northBound.step4();
		southBound.step4();
		
		if(debug)
		{
			System.out.println("-----------Step 4------------");
			System.out.println("Hadj,eb1 :"+eastBound.lane1.saturationHeadwayDeparture);
			if(eastBound.lane2!=null)
				System.out.println("Hadj, eb2 :"+eastBound.lane2.saturationHeadwayDeparture);
			if(eastBound.lane3!=null)
				System.out.println("Hadj, eb3 : "+eastBound.lane3.saturationHeadwayDeparture);
			System.out.println();
			System.out.println("Hadj, wb1 :"+westBound.lane1.saturationHeadwayDeparture);
			if(westBound.lane2!=null)
				System.out.println("Hadj, wb2 :"+westBound.lane2.saturationHeadwayDeparture);
			if(westBound.lane3!=null)
				System.out.println("Hadj, wb3 : "+westBound.lane3.saturationHeadwayDeparture);
			System.out.println();
			System.out.println("Hadj, nb1 :"+northBound.lane1.saturationHeadwayDeparture);
			if(northBound.lane2!=null)
				System.out.println("Hadj, nb2 :"+northBound.lane2.saturationHeadwayDeparture);
			if(northBound.lane3!=null)
				System.out.println("Hadj, nb3 : "+northBound.lane3.saturationHeadwayDeparture);
			System.out.println();
			System.out.println("Hadj, sb1 :"+southBound.lane1.saturationHeadwayDeparture);
			if(southBound.lane2!=null)
				System.out.println("Hadj, sb2 :"+southBound.lane2.saturationHeadwayDeparture);
			if(southBound.lane3!=null)
				System.out.println("Hadj, sb3 : "+southBound.lane3.saturationHeadwayDeparture);
			System.out.println("-------End of Step 4---------"+'\n');
			
		}
		
		//step 5 to 11
		boolean converged = false;
		int i = 1;
		while (!converged && i < 400) {
			Test.counter = i;
			i++;
			eastBound.degreeOfUtilize();
			westBound.degreeOfUtilize();
			northBound.degreeOfUtilize();
			southBound.degreeOfUtilize();
			converged = true;
			converged &= eastBound.compute();
			converged &= westBound.compute();
			converged &= northBound.compute();
			converged &= southBound.compute();
		}
		
		if(debug)
		{
			System.out.println("-----------Step 5------------");
			System.out.println("Initial Departure Headway is: 3.2");
			System.out.println("-------End of Step 5---------"+'\n');	
		}
		

		if (!converged) {
			System.err.println("Unable to converge for AWSC");
		}
		
		//step 12, 13, 14, 16
		eastBound.finalSteps();
		westBound.finalSteps();
		northBound.finalSteps();
		southBound.finalSteps();
		
		if(debug)
		{
			System.out.println("-----------Step 11------------");
			System.out.println("Hd,eb1 :"+eastBound.lane1.getIntialHeadwayDeparture());
			if(eastBound.lane2!=null)
				System.out.println("Hd,eb2 :"+eastBound.lane2.getIntialHeadwayDeparture());
			if(eastBound.lane3!=null)
				System.out.println("Hd,eb3 :"+eastBound.lane3.getIntialHeadwayDeparture());
			System.out.println();
			System.out.println("Hd,wb4 :"+westBound.lane1.getIntialHeadwayDeparture());
			if(westBound.lane2!=null)
				System.out.println("Hd,wb5 :"+westBound.lane2.getIntialHeadwayDeparture());
			if(westBound.lane3!=null)
				System.out.println("Hd,wb6 :"+westBound.lane3.getIntialHeadwayDeparture());
			System.out.println();
			System.out.println("Hd,nb7 :"+northBound.lane1.getIntialHeadwayDeparture());
			if(northBound.lane2!=null)
				System.out.println("Hd,nb8 :"+northBound.lane2.getIntialHeadwayDeparture());
			if(northBound.lane3!=null)
				System.out.println("Hd,nb9 :"+northBound.lane3.getIntialHeadwayDeparture());
			System.out.println();
			System.out.println("Hd,sb10 :"+southBound.lane1.getIntialHeadwayDeparture());
			if(southBound.lane2!=null)
				System.out.println("Hd,sb11 :"+southBound.lane2.getIntialHeadwayDeparture());
			if(southBound.lane3!=null)
				System.out.println("Hd,sb12 :"+southBound.lane3.getIntialHeadwayDeparture());
			System.out.println("-------End of Step 11---------"+'\n');
			
			System.out.println("-----------Step 13------------");
			System.out.println("t,eb1 :"+eastBound.lane1.serviceTime);
			if(eastBound.lane2!=null)
				System.out.println("t,eb2 :"+eastBound.lane2.serviceTime);
			if(eastBound.lane3!=null)
				System.out.println("t,eb3 :"+eastBound.lane3.serviceTime);
			System.out.println();
			System.out.println("t,wb4 :"+westBound.lane1.serviceTime);
			if(westBound.lane2!=null)
				System.out.println("t,wb5 :"+westBound.lane2.serviceTime);
			if(westBound.lane3!=null)
				System.out.println("t,wb6 :"+westBound.lane3.serviceTime);
			System.out.println();
			System.out.println("t,nb7 :"+northBound.lane1.serviceTime);
			if(northBound.lane2!=null)
				System.out.println("t,nb8 :"+northBound.lane2.serviceTime);
			if(northBound.lane3!=null)
				System.out.println("t,nb9 :"+northBound.lane3.serviceTime);
			System.out.println();
			System.out.println("t,sb10 :"+southBound.lane1.serviceTime);
			if(southBound.lane2!=null)
				System.out.println("t,sb11 :"+southBound.lane2.serviceTime);
			if(southBound.lane3!=null)
				System.out.println("t,sb12 :"+southBound.lane3.serviceTime);
			System.out.println("-------End of Step 13---------"+'\n');
			
			System.out.println("-----------Step 14------------");
			System.out.println("d,eb1 :"+eastBound.lane1.delay);
			if(eastBound.lane2!=null)
				System.out.println("d,eb2 :"+eastBound.lane2.delay);
			if(eastBound.lane3!=null)
				System.out.println("d,eb3 :"+eastBound.lane3.delay);
			System.out.println();
			System.out.println("d,wb4 :"+westBound.lane1.delay);
			if(westBound.lane2!=null)
				System.out.println("d,wb5 :"+westBound.lane2.delay);
			if(westBound.lane3!=null)
				System.out.println("d,wb6 :"+westBound.lane3.delay);
			System.out.println();
			System.out.println("d,nb7 :"+northBound.lane1.delay);
			if(northBound.lane2!=null)
				System.out.println("d,nb8 :"+northBound.lane2.delay);
			if(northBound.lane3!=null)
				System.out.println("d,nb9 :"+northBound.lane3.delay);
			System.out.println();
			System.out.println("d,sb10 :"+southBound.lane1.delay);
			if(southBound.lane2!=null)
				System.out.println("d,sb11 :"+southBound.lane2.delay);
			if(southBound.lane3!=null)
				System.out.println("d,sb12 :"+southBound.lane3.delay);
			System.out.println("-------End of Step 14---------"+'\n');
			
			System.out.println("-----------Step 15------------");
			System.out.println("d,eb :"+eastBound.totalDelay);
			System.out.println("d,wb :"+westBound.totalDelay);
			System.out.println("d,nb :"+northBound.totalDelay);
			System.out.println("d,sb :"+southBound.totalDelay);
			System.out.println("-------End of Step 15---------"+'\n');
		}
	
		//step 15
		AWSCBound.computeFinalDelay(eastBound,westBound,northBound,southBound); 
		if(debug)
		{
			System.out.println("-----------Step 15b------------");
			System.out.println("Total Intersaction Delay :"+AWSCBound.intersectionDelay);
			System.out.println("-------End of Step 15b---------"+'\n');
			
			System.out.println("-----------Step 16------------");
			System.out.println("Q95,eb1 :"+eastBound.lane1.lanePercentile);
			if(eastBound.lane2!=null)
				System.out.println("Q95,eb2 :"+eastBound.lane2.lanePercentile);
			if(eastBound.lane3!=null)
				System.out.println("Q95,eb3 : "+eastBound.lane3.lanePercentile);
			System.out.println();
			System.out.println("Q95,wb4 :"+westBound.lane1.lanePercentile);
			if(westBound.lane2!=null)
				System.out.println("Q95,wb5 :"+westBound.lane2.lanePercentile);
			if(westBound.lane3!=null)
				System.out.println("Q95,wb6 : "+westBound.lane3.lanePercentile);
			System.out.println();
			System.out.println("d,nb7 :"+northBound.lane1.lanePercentile);
			if(northBound.lane2!=null)
				System.out.println("Q95,nb8 :"+northBound.lane2.lanePercentile);
			if(northBound.lane3!=null)
				System.out.println("Q95,nb9 : "+northBound.lane3.lanePercentile);
			System.out.println();
			System.out.println("Q95,sb10 :"+southBound.lane1.lanePercentile);
			if(southBound.lane2!=null)
				System.out.println("Q95,sb11 :"+southBound.lane2.lanePercentile);
			if(southBound.lane3!=null)
				System.out.println("Q95,sb12 : "+southBound.lane3.lanePercentile);
			System.out.println("-------End of Step 16---------"+'\n');
			
			
		}
		output.add(eastBound);
		output.add(westBound);
		output.add(northBound);
		output.add(southBound);
		
		return output;

	}

}
