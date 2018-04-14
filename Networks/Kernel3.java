package Networks;

public class Kernel3 {
	private double[][][] kernel;
	private int lkernel;
	public Kernel3(int lkernel)
	{
		this.lkernel=lkernel;
		kernel=new double[3][lkernel][lkernel];
		int i,j,k;
		for(i=0;i<3;i++)
		{
			for(j=0;j<lkernel;j++)
			{
				for(k=0;k<lkernel;k++)
				{
					kernel[i][j][k]=(double)(Math.random()*0.01-0.005);	//from -0.005 to 0.005
				}
			}
		}
	}
	public double[][][] getkernel()
	{
		return kernel;
	}
}
