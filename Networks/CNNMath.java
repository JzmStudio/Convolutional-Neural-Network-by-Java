package Networks;

public class CNNMath {
	public static final double studySpeed=0.1f;
	public static final double studySpeedH=0.1f;
	public static final double momentum=0.3f;
	
	public static double sigmoid(double x)
	{
		return (double) (1.0/(1.0+Math.exp(x*(-1.0))));
	}
	
	/**
	 * 步长为1|不补零|矩阵相乘|实现中k1比k2大!
	 * @param k1
	 * @param k2
	 * @return
	 */
	public static double[][] mul(double[][] k1,double[][] k2)
	{
		int i,j,x,y;
		double[][] r=new double[k1.length-k2.length+1][k1[0].length-k2[0].length+1];
		for(i=0;i<r.length;i++)
		{
			for(j=0;j<r[i].length;j++)
			{
				for(x=0;x<k2.length;x++)
				{
					for(y=0;y<k2[x].length;y++)
					{
						r[i][j]+=k1[i+x][j+y]*k2[x][y];
					}
				}
			}
		}
		return r;
	}
	
	/**
	 * 矩阵求和
	 * @param k
	 * @return
	 */
	public static double sum(double[][] k)
	{
		int i,j;
		double result=0;
		for(i=0;i<k.length;i++)
		{
			for(j=0;j<k[i].length;j++)
			{
				result+=k[i][j];
			}
		}
		return result;
	}
	
	/**
	 * 计算k1卷积k2  实现中k1.length>k2.length!
	 * @param k1
	 * @param k2
	 * @return
	 */
	public static double[][] cov2(double[][] k1,double[][] k2)
	{
		int i,j,x,y;
		double[][] r=new double[k1.length-k2.length+1][k1[0].length-k2[0].length+1];
		for(i=0;i<r.length;i++)
		{
			for(j=0;j<r[i].length;j++)
			{
				for(x=0;x<k2.length;x++)
				{
					for(y=0;y<k2[x].length;y++)
					{
						r[i][j]+=k1[i+x][j+y]*k2[k2.length-1-x][k2[x].length-1-y];
					}
				}
			}
		}
		return r;
	}
	
	/**
	 * 将矩阵两边等量填充0(需原矩阵长宽相等)
	 * @param src 需要变换的矩阵
	 * @param sum !注意!length为矩阵每侧填充的0的个数
	 * @return
	 */
	public static double[][] full(double[][] src,int sum)
	{
		int length=src.length+2*sum;
		double [][] r=new double[length][length];
		int i,j;
		for(i=0;i<src.length;i++)
		{
			for(j=0;j<src.length;j++)
			{
				r[i+sum][j+sum]=src[i][j];
			}
		}
		return r;
	}
	
	/**
	 * Print 2-dimension array
	 * @param kernel
	 */
	public static void printKernel(double[][] kernel)
	{
		int i,j;
		for(i=0;i<kernel.length;i++)
		{
			for(j=0;j<kernel[i].length;j++)
			{
				System.out.print(""+kernel[i][j]+" ");
			}
			System.out.println();
		}
	}
	
	public static double[][][] copy(double[][][] src)
	{
		double[][][] r=new double[src.length][src[0].length][src[0][0].length];
		int i,j,k;
		for(i=0;i<src.length;i++)
		{
			for(j=0;j<src[i].length;j++)
			{
				for(k=0;k<src[i][j].length;k++)
				{
					r[i][j][k]=src[i][j][k];
				}
			}
		}
		return r;
	}
	
	public static void clearToZero(double[][] src)
	{
		int i,j;
		for(i=0;i<src.length;i++)
		{
			for(j=0;j<src[i].length;j++)
			{
				src[i][j]=0;
			}
		}
	}
}
