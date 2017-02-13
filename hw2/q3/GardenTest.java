public class GardenTest {
	protected static class Newton implements Runnable{
		Garden garden;
		public Newton(Garden garden){
			this.garden = garden;
		}
		@Override
		public void run() {
		    while (true) {
                try  {
                    garden.startDigging();
                } catch (InterruptedException e){}
			    dig();
                garden.doneDigging();
			}
		} 
		
		private void dig(){
		}
	}
	
	protected static class Benjamin implements Runnable {
		Garden garden;
		public Benjamin(Garden garden){
			this.garden = garden;
		}
		@Override
		public void run() {
		    while (true) {
                try  {
                    garden.startSeeding();
                } catch (InterruptedException e){}
				plantSeed();
				garden.doneSeeding();
			}
		} 
		
		private void plantSeed(){
		}
	}
	
	protected static class Mary implements Runnable {
		Garden garden;
		public Mary(Garden garden){
            this.garden = garden;
		}
		@Override
		public void run() {
		    while (true) {
                try  {
                    garden.startFilling();
                } catch (InterruptedException e){}
			 	Fill();
			 	garden.doneFilling();
			}
		} 
		
		private void Fill(){
		}
	}

    public static void main (String[] args) {
        Garden g = new Garden();
        Thread t1 = new Thread(new Newton(g));
        Thread t2 = new Thread(new Benjamin(g));
        Thread t3 = new Thread(new Mary(g));
        t1.start();
        t2.start();
        t3.start();
        try {
            t1.join();
        } catch (InterruptedException e) {}
        try {
            t2.join();
        } catch (InterruptedException e) {}
        try {
            t3.join();
        } catch (InterruptedException e) {}

    }
}
