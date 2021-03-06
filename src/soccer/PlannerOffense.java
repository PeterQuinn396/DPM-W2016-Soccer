package soccer;

/**
 * 
 * @author Peter Quinn
 *
 */
public class PlannerOffense {

	private Odometer odometer;
	private Navigation nav;
	private Sensors sensors;
	private Motors motors;
	private int BC;
	private int[] ballZone;
	private int attackLine;
	private LauncherController launcher;

	private int xTileMax = 11;
	private int yTileMax = 11;
	int llX;
	int llY;
	int urX;
	
	int urY;
	int ballColorID;

	/**
	 * 
	 * @param odometer
	 * @param nav
	 * @param sensors
	 * @param motors
	 */
	public PlannerOffense(Odometer odometer, Navigation nav, Sensors sensors, Motors motors, int attackLine, int BC,
			int[] ballZone, int ballColorID) {
		this.odometer = odometer;
		this.nav = nav;
		this.sensors = sensors;
		this.motors = motors;
		this.BC = BC;
		this.ballZone = ballZone; // {llX,llY,urX,urY}
		this.attackLine = attackLine;
		this.launcher = launcher;
		this.llX =ballZone[0];
		this.llY=ballZone[1];
		this.urX=ballZone[2];
		this.urY=ballZone[3];
		this.ballColorID = ballColorID;
	}

	// have the robot execute its offense procedure
	public void run() {

		// move around outside to get beside attack zone
		double attackLineY = attackLine * PhysicalConstants.TILE_SPACING;
		if (odometer.getY() > attackLineY) {// above attacker line, move down
											// and past
			nav.movePastY(attackLineY, true, false);

		} else {// start below zone, move up to just before the line
			nav.movePastY(attackLineY - PhysicalConstants.TILE_SPACING, true, false);
		}

		// move to middle of attack zone
		double centerX = xTileMax / 2 * PhysicalConstants.TILE_SPACING;
		double centerY = attackLineY - PhysicalConstants.TILE_SPACING / 2;
		nav.travelTo(centerX, centerY, true, false);

		//go get balls
		
		new BallPickupController(ballColorID, odometer, nav, launcher, sensors, motors, llX, llY, urX, urY).navigateToPlatform();
		
		//shoot on net
		
		new AttackNet(launcher, nav, centerX, centerY, sensors, motors, odometer).run();
	}

}
