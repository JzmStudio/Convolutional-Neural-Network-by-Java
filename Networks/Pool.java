package Networks;

public class Pool {
	private double[][] in;
	private double[][] out;
	private int inlen;	//����˵ĳ���
	private int outlen;	//����˵ĳ���

	/**
	 * 
	 * @param in
	 * @param out if you do not know what will out,it is null
	 */
	public Pool(double[][] in,double[][] out)
	{
		this.in=in;
		this.inlen=in.length;
		this.out=out;
		sample();
	}
	
	public double[][] sample()
	{
		this.inlen=in.length;
		samplePri();
		return out;
	}
	
	/*����ƽ��ֵ�ػ�*/
	private void samplePri()
	{
		int i,j,x,y;
		double sum=0;
		if(out==null)
		{
			if(inlen%2!=0)	//��Ϊż��0
			{
				double[][] ch=new double[inlen+1][inlen+1];
				for(i=0;i<inlen;i++)
				{
					for(j=0;j<inlen;j++)
					{
						ch[i][j]=in[i][j];
					}
				}
				in=ch;
				outlen=(inlen+1)/2;
			}
			else
			{
				outlen=inlen/2;
			}
			this.out=new double[outlen][outlen];
		}
		//���Ѿ���ʼ������
		for(i=0;i<outlen;i++)
		{
			for(j=0;j<outlen;j++)
			{
				sum=0;
				for(x=0;x<2;x++)
				{
					for(y=0;y<2;y++)
					{
						sum+=in[i*2+x][j*2+y];
					}
				}
				out[i][j]=sum/4;
			}
		}
	}
	
	public double[][] getSample()
	{
		return out;
	}
	
	public int getResultLength()
	{
		return outlen;
	}
	
	/**
	 * ǰ��Ϊȫ���Ӳ�
	 * @param k5 ȫ���Ӳ��Ȩֵ
	 * @param e5 ȫ���Ӳ�ڵ�����
	 * @param r4 ����ڵ�����ֵ
	 * @return	��������
	 */
	public static double[][][] error(double[][][][] k5,double[] e5,double[][][] r4)
	{
		double[][][] e=new double[k5[0].length][k5[0][0].length][k5[0][0][0].length];
		int i,j,z,k;
		for(k=0;k<k5.length;k++)
		{
			for(i=0;i<k5[k].length;i++)
			{
				for(j=0;j<k5[k][i].length;j++)
				{
					for(z=0;z<k5[k][i][j].length;z++)
					{
						e[i][j][z]+=r4[i][j][z]*(1-r4[i][j][z])*e5[k]*k5[k][i][j][z];
					}
				}
			}
		}
		return e;
	}
	
	/**
	 * ǰ��Ϊ�����
	 * @param k3 Ϊǰ������ľ����
	 * @param e3 Ϊǰ����������
	 * @param r4 Ϊ��������ֵ
	 * @return ��������
	 */
	/*public static double[][][] errorFromCov(double[][][] k3,double[][][] e3,double[][][] r4)
	{
		
	}*/
}
