package Networks;

public class KernelBuilder {
	public static double[][] newKernel(int length)
	{
		int i,j;
		double[][] kernel=new double[length][length];
		for(i=0;i<length;i++)
		{
			for(j=0;j<length;j++)
			{
				kernel[i][j]=(double) (Math.random()*0.1-0.05);	//from -0.5 to 0.5
			}
		}
		return kernel;
	}
	
	/**
	 * 
	 * @param Sum 二维矩阵的数量
	 * @param lengthIn2D 每个二维矩阵的长度
	 * @return
	 */
	public static double[][][] newKernel3(int Sum,int lengthIn2D)
	{
		int k;
		double[][][]	kernel=new double[Sum][][];
		for(k=0;k<Sum;k++)
		{
			kernel[k]=newKernel(lengthIn2D);
		}
		return kernel;
	}
	public static double newBias()
	{
		return 0;
	}
}
