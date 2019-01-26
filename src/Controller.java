import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class Controller {

	public static void main(String[] args) {
		
		//double[] volumes = {50.0,300.00,0.0,0.0,300.00,100.00,0.00,0.00,0.00,100.0,0.00,50.00};
		//double[] volumes = {14.0,38.00,16.0,39.0,23.00,18.00,19.00,41.00,29.00,12.0,31.00,22.00};
		double volumes[] = new double[15];
		int[] noOfLanes = { 2, 2, 3, 3 };
		//int[] noOfLanes = { 1, 1, 1, 1 };
		
		ArrayList<AWSCBound> bounds = new ArrayList<>();
		long startTime = System.currentTimeMillis();
		
		String Query = "Update AWSC set "
				+ "`EB_L_Demand_Flow_Rate_veh/hr`=?, `EB_T_Demand_Flow_Rate_veh/hr`=?, `EB_R_Demand_Flow_Rate_veh/hr`=?, "
				+ "`WB_L_Demand_Flow_Rate_veh/hr`=?, `WB_T_Demand_Flow_Rate_veh/hr`=?, `WB_R_Demand_Flow_Rate_veh`=?, "
				+ "`NB_L_Demand_Flow_Rate_veh`=?, `NB_T_Demand_Flow_Rate_veh`=?, `NB_R_Demand_Flow_Rate_veh/hr`=?, "
				+ "`SB_L_Demand_Flow_Rate_veh/hr`=?, `SB_T_Demand_Flow_Rate_veh`=?, `SB_R_Demand_Flow_Rate_veh`=?, "
				+ "`EB_Total_Demand_Flow_Rate`=?, `WB_Total_Demand_Flow_Rate`=?, `NB_Total_Demand_Flow_Rate`=?, `SB_Total_Demand_Flow_Rate`=?, "
				+ "`Geometry_Group`=?, "
				+ "`EB_Highway_AF_1`=?, `EB_Highway_AF_2`=?, `EB_Highway_AF_3`=?, "
				+ "`WB_Highway_AF_4`=?, `WB_Highway_AF_5`=?, `WB_Highway_AF_6`=?, "
				+ "`NB_Highway_AF_7`=?, `NB_Highway_AF_8`=?, `NB_Highway_AF_9`=?, "
				+ "`SB_Highway_AF_10`=?, `SB_Highway_AF_11`=?, `SB_Highway_AF_12`=?, "
				+ "`Initial_Departure_headway`=?, "
				+ "`EB_Headway_Departure_1`=?, `EB_Headway_Departure_2`=?, `EB_Headway_Departure_3`=?, "
				+ "`WB_Headway_Departure_4`=?, `WB_Headway_Departure_5`=?, `WB_Headway_Departure_6`=?, "
				+ "`NB_Headway_Departure_7`=?, `NB_Headway_Departure_8`=?, `NB_Headway_Departure_9`=?, "
				+ "`SB_Headway_Departure_10`=?, `SB_Headway_Departure_11`=?, `SB_Headway_Departure_12`=?, "
				+ "`EB_Service_Time_1`=?, `EB_Service_Time_2`=?, `EB_Service_Time_3`=?, "
				+ "`WB_Service_Time_4`=?, `WB_Service_Time_5`=?, `WB_Service_Time_6`=?, "
				+ "`NB_Service_Time_7`=?, `NB_Service_Time_8`=?, `NB_Service_Time_9`=?, "
				+ "`SB_Service_Time_10`=?, `SB_Service_Time_11`=?, `SB_Service_Time_12`=?, "
				+ "`EB_Delay_1`=?, `EB_Delay_2`=?, `EB_Delay_3`=?, "
				+ "`WB_Delay_4`=?, `WB_Delay_5`=?, `WB_Delay_6`=?, "
				+ "`NB_Delay_7`=?, `NB_Delay_8`=?, `NB_Delay_9`=?, "
				+ "`SB_Delay_10`=?, `SB_Delay_11`=?, `SB_Delay_12`=?, "
				+ "`EB_Total_Delay`=?, `WB_Total_Delay`=?, `NB_Total_Delay`=?, `SB_Total_Delay`=?, "
				+ "`Total_Intersaction_Delay`=?, "
				+ "`EB_Q95_1`=?, `EB_Q95_2`=?, `EB_Q95_3`=?, "
				+ "`WB_Q95_4`=?, `WB_Q95_5`=?, `WB_Q95_6`=?, "
				+ "`NB_Q95_7`=?, `NB_Q95_8`=?, `NB_Q95_9`=?, "
				+ "`SB_Q95_10`=?, `SB_Q95_11`=?, `SB_Q95_12` =? "
				+ " where Number =? ";
		
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/RBT","root","");
			Statement st=con.createStatement();
			ResultSet rs=st.executeQuery("select * from AWSC");
			
			PreparedStatement updateData = con.prepareStatement(Query);
			int k=1;
			
			while(rs.next())
			{
				volumes[0] = rs.getDouble(2);
				volumes[1] = rs.getDouble(3);
				volumes[2] = rs.getDouble(4);
				volumes[3] = rs.getDouble(5);
				volumes[4] = rs.getDouble(6);
				volumes[5] = rs.getDouble(7);
				volumes[6] = rs.getDouble(8);
				volumes[7] = rs.getDouble(9);
				volumes[8] = rs.getDouble(10);
				volumes[9] = rs.getDouble(11);
				volumes[10] = rs.getDouble(12);
				volumes[11] = rs.getDouble(13);
				noOfLanes[0] = rs.getInt(14);
				noOfLanes[1] = rs.getInt(15);
				noOfLanes[2] = rs.getInt(16);
				noOfLanes[3] = rs.getInt(17);
				volumes[12] = rs.getInt(18);
				volumes[13] = rs.getDouble(19);
				volumes[14] = rs.getDouble(20);
				
				AWSC awsc = new AWSC(volumes, noOfLanes);
				
				//System.out.println("************* Number "+k+" ***************");
				bounds = awsc.computeSteps();
				//System.out.println("********** End of Number "+k+" ***********\n");
				/*-------------------------------------------------------------*/
				updateData.setDouble(1, bounds.get(0).lane1.flowLeftVolume);
				
				if(bounds.get(0).lane3!=null)
				{
					updateData.setDouble(2, bounds.get(0).lane2.flowThroughVolume);
					updateData.setDouble(3, bounds.get(0).lane3.flowRightVolume);
				}
				else if(bounds.get(0).lane2!=null)
				{
					updateData.setDouble(2, bounds.get(0).lane2.flowThroughVolume);
					updateData.setDouble(3, bounds.get(0).lane2.flowRightVolume);
				}
				else
				{
					updateData.setDouble(2, bounds.get(0).lane1.flowThroughVolume);
					updateData.setDouble(3, bounds.get(0).lane1.flowRightVolume);
				}
				/*-------------------------------------------------------------*/
				updateData.setDouble(4, bounds.get(1).lane1.flowLeftVolume);
				if(bounds.get(1).lane3!=null)
				{
					updateData.setDouble(5, bounds.get(1).lane2.flowThroughVolume);
					updateData.setDouble(6, bounds.get(1).lane3.flowRightVolume);
				}
				else if(bounds.get(1).lane2!=null)
				{
					updateData.setDouble(5, bounds.get(1).lane2.flowThroughVolume);
					updateData.setDouble(6, bounds.get(1).lane2.flowRightVolume);
				}
				else
				{
					updateData.setDouble(5, bounds.get(1).lane1.flowThroughVolume);
					updateData.setDouble(6, bounds.get(1).lane1.flowRightVolume);
				}
				/*-------------------------------------------------------------*/
				updateData.setDouble(7, bounds.get(2).lane1.flowLeftVolume);
				if(bounds.get(2).lane3!=null)
				{
					updateData.setDouble(8, bounds.get(2).lane2.flowThroughVolume);
					updateData.setDouble(9, bounds.get(2).lane3.flowRightVolume);
				}
				else if(bounds.get(2).lane2!=null)
				{
					updateData.setDouble(8, bounds.get(2).lane2.flowThroughVolume);
					updateData.setDouble(9, bounds.get(2).lane2.flowRightVolume);
				}
				else
				{
					updateData.setDouble(8, bounds.get(2).lane1.flowThroughVolume);
					updateData.setDouble(9, bounds.get(2).lane1.flowRightVolume);
				}
				/*-------------------------------------------------------------*/
				updateData.setDouble(10, bounds.get(3).lane1.flowLeftVolume);
				if(bounds.get(3).lane3!=null)
				{
					updateData.setDouble(11, bounds.get(3).lane2.flowThroughVolume);
					updateData.setDouble(12, bounds.get(3).lane3.flowRightVolume);
				}
				else if(bounds.get(3).lane2!=null)
				{
					updateData.setDouble(11, bounds.get(3).lane2.flowThroughVolume);
					updateData.setDouble(12, bounds.get(3).lane2.flowRightVolume);
				}
				else
				{
					updateData.setDouble(11, bounds.get(3).lane1.flowThroughVolume);
					updateData.setDouble(12, bounds.get(3).lane1.flowRightVolume);
				}
				/*-------------------------------------------------------------*/
				
				updateData.setDouble(13, bounds.get(0).totalVolume());
				updateData.setDouble(14, bounds.get(1).totalVolume());
				updateData.setDouble(15, bounds.get(2).totalVolume());
				updateData.setDouble(16, bounds.get(3).totalVolume());
				
				/*-------------------------------------------------------------*/
				
				updateData.setString(17, bounds.get(0).geometryAwsc.toString());
				
				/*-------------------------------------------------------------*/
				updateData.setDouble(18, bounds.get(0).lane1.flowLeftVolume);
				updateData.setDouble(19, (bounds.get(0).lane2!=null)?bounds.get(0).lane2.saturationHeadwayDeparture:0);
				updateData.setDouble(20, (bounds.get(0).lane3!=null)?bounds.get(0).lane3.saturationHeadwayDeparture:0);
				
				updateData.setDouble(21, bounds.get(1).lane1.flowLeftVolume);
				updateData.setDouble(22, (bounds.get(1).lane2!=null)?bounds.get(1).lane2.saturationHeadwayDeparture:0);
				updateData.setDouble(23, (bounds.get(1).lane3!=null)?bounds.get(1).lane3.saturationHeadwayDeparture:0);
				
				updateData.setDouble(24, bounds.get(2).lane1.flowLeftVolume);
				updateData.setDouble(25, (bounds.get(2).lane2!=null)?bounds.get(2).lane2.saturationHeadwayDeparture:0);
				updateData.setDouble(26, (bounds.get(2).lane3!=null)?bounds.get(2).lane3.saturationHeadwayDeparture:0);
				
				updateData.setDouble(27, bounds.get(3).lane1.flowLeftVolume);
				updateData.setDouble(28, (bounds.get(3).lane2!=null)?bounds.get(3).lane2.saturationHeadwayDeparture:0);
				updateData.setDouble(29, (bounds.get(3).lane3!=null)?bounds.get(3).lane3.saturationHeadwayDeparture:0);
					
				/*-------------------------------------------------------------*/
				
				updateData.setDouble(30, 3.2);
				
				/*-------------------------------------------------------------*/
				
				updateData.setDouble(31, bounds.get(0).lane1.getIntialHeadwayDeparture());
				updateData.setDouble(32, (bounds.get(0).lane2!=null)?bounds.get(0).lane2.getIntialHeadwayDeparture():0);
				updateData.setDouble(33, (bounds.get(0).lane3!=null)?bounds.get(0).lane3.getIntialHeadwayDeparture():0);
				
				updateData.setDouble(34, bounds.get(1).lane1.getIntialHeadwayDeparture());
				updateData.setDouble(35, (bounds.get(1).lane2!=null)?bounds.get(1).lane2.getIntialHeadwayDeparture():0);
				updateData.setDouble(36, (bounds.get(1).lane3!=null)?bounds.get(1).lane3.getIntialHeadwayDeparture():0);
				
				updateData.setDouble(37, bounds.get(2).lane1.getIntialHeadwayDeparture());
				updateData.setDouble(38, (bounds.get(2).lane2!=null)?bounds.get(2).lane2.getIntialHeadwayDeparture():0);
				updateData.setDouble(39, (bounds.get(2).lane3!=null)?bounds.get(2).lane3.getIntialHeadwayDeparture():0);
				
				updateData.setDouble(40, bounds.get(3).lane1.getIntialHeadwayDeparture());
				updateData.setDouble(41, (bounds.get(3).lane2!=null)?bounds.get(3).lane2.getIntialHeadwayDeparture():0);
				updateData.setDouble(42, (bounds.get(3).lane3!=null)?bounds.get(3).lane3.getIntialHeadwayDeparture():0);
				
				/*-------------------------------------------------------------*/
				
				updateData.setDouble(43, bounds.get(0).lane1.serviceTime);
				updateData.setDouble(44, (bounds.get(0).lane2!=null)?bounds.get(0).lane2.serviceTime:0);
				updateData.setDouble(45, (bounds.get(0).lane3!=null)?bounds.get(0).lane3.serviceTime:0);
				
				updateData.setDouble(46, bounds.get(1).lane1.serviceTime);
				updateData.setDouble(47, (bounds.get(1).lane2!=null)?bounds.get(1).lane2.serviceTime:0);
				updateData.setDouble(48, (bounds.get(1).lane3!=null)?bounds.get(1).lane3.serviceTime:0);
				
				updateData.setDouble(49, bounds.get(2).lane1.serviceTime);
				updateData.setDouble(50, (bounds.get(2).lane2!=null)?bounds.get(2).lane2.serviceTime:0);
				updateData.setDouble(51, (bounds.get(2).lane3!=null)?bounds.get(2).lane3.serviceTime:0);
				
				updateData.setDouble(52, bounds.get(3).lane1.serviceTime);
				updateData.setDouble(53, (bounds.get(3).lane2!=null)?bounds.get(3).lane2.serviceTime:0);
				updateData.setDouble(54, (bounds.get(3).lane3!=null)?bounds.get(3).lane3.serviceTime:0);

				/*-------------------------------------------------------------*/
				
				updateData.setDouble(55, bounds.get(0).lane1.delay);
				updateData.setDouble(56, (bounds.get(0).lane2!=null)?bounds.get(0).lane2.delay:0);
				updateData.setDouble(57, (bounds.get(0).lane3!=null)?bounds.get(0).lane3.delay:0);
				
				updateData.setDouble(58, bounds.get(1).lane1.delay);
				updateData.setDouble(59, (bounds.get(1).lane2!=null)?bounds.get(1).lane2.delay:0);
				updateData.setDouble(60, (bounds.get(1).lane3!=null)?bounds.get(1).lane3.delay:0);
				
				updateData.setDouble(61, bounds.get(2).lane1.delay);
				updateData.setDouble(62, (bounds.get(2).lane2!=null)?bounds.get(2).lane2.delay:0);
				updateData.setDouble(63, (bounds.get(2).lane3!=null)?bounds.get(2).lane3.delay:0);
				
				updateData.setDouble(64, bounds.get(3).lane1.delay);
				updateData.setDouble(65, (bounds.get(3).lane2!=null)?bounds.get(3).lane2.delay:0);
				updateData.setDouble(66, (bounds.get(3).lane3!=null)?bounds.get(3).lane3.delay:0);
				
				/*-------------------------------------------------------------*/
				
				updateData.setDouble(67, bounds.get(0).totalDelay);
				updateData.setDouble(68, bounds.get(1).totalDelay);
				updateData.setDouble(69, bounds.get(2).totalDelay);
				updateData.setDouble(70, bounds.get(3).totalDelay);
				
				/*-------------------------------------------------------------*/
				
				updateData.setDouble(71, AWSCBound.intersectionDelay);
				
				/*-------------------------------------------------------------*/
				
				updateData.setDouble(72, bounds.get(0).lane1.lanePercentile);
				updateData.setDouble(73, (bounds.get(0).lane2!=null)?bounds.get(0).lane2.lanePercentile:0);
				updateData.setDouble(74, (bounds.get(0).lane3!=null)?bounds.get(0).lane3.lanePercentile:0);
				
				updateData.setDouble(75, bounds.get(1).lane1.lanePercentile);
				updateData.setDouble(76, (bounds.get(1).lane2!=null)?bounds.get(1).lane2.lanePercentile:0);
				updateData.setDouble(77, (bounds.get(1).lane3!=null)?bounds.get(1).lane3.lanePercentile:0);
				
				updateData.setDouble(78, bounds.get(2).lane1.lanePercentile);
				updateData.setDouble(79, (bounds.get(2).lane2!=null)?bounds.get(2).lane2.lanePercentile:0);
				updateData.setDouble(80, (bounds.get(2).lane3!=null)?bounds.get(2).lane3.lanePercentile:0);
				
				updateData.setDouble(81, bounds.get(3).lane1.lanePercentile);
				updateData.setDouble(82, (bounds.get(3).lane2!=null)?bounds.get(3).lane2.lanePercentile:0);
				updateData.setDouble(83, (bounds.get(3).lane3!=null)?bounds.get(3).lane3.lanePercentile:0);
				
				/*-------------------------------------------------------------*/
				
				updateData.setInt(84, k );
				
				/*-------------------------------------------------------------*/
				updateData.executeUpdate();
				k++;
				
				
			}
			long endTime= System.currentTimeMillis();
			System.out.println("Time taken: "+(endTime-startTime));

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		

	}

}
