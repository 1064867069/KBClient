using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Text.RegularExpressions;
using System.IO;
using Kerberos.Authentication;
using System.Threading;
using System.Net;
using System.Net.Sockets;

namespace Kerberos
{
    class Program
    {
        static void Main(string[] args)
        {
            string ptxt,ctxt,detxt;
            //FileStream fs = new FileStream("G:\\代码\\C#\\Kerberos\\Kerberos\\AStoC.txt", FileMode.Open, FileAccess.Read);
            //StreamReader sr = new StreamReader(fs);
            //Console.WriteLine("请输入一个字符串：\n");
            //ptxt = Console.ReadLine();
            //ctxt = DES.Tool.txtDES(ptxt, true, "39454107kkdl");
            //Console.WriteLine("加密字符串：" + ctxt);
            //detxt = DES.Tool.txtDES(ctxt, false, "39454107kkdl");
            //Console.WriteLine("解密字符串：" + detxt);
            //Console.WriteLine(Authentication.AuthTool.GetInternalIP());
            //testip();
            //Console.WriteLine(DateTime.Now.ToString());

            //testhead();
            testAS();
            //TGS_Auth.Auth(sr.ReadLine());
            //sr.Close();
            //fs.Close();
            //AuthTool.ModTick("ABCDE12345678we   z");
            Console.ReadKey();
        }

        static void testip()
        {
            string ip = "192.147.43.125";
            Console.WriteLine(Authentication.AuthTool.GetIpLast(ip));
        }
        static void testhead()
        {
            byte[] head = Authentication.AS_Auth.Gethead(8, "192.147.43.121");
            foreach(byte n in head)
            {
                Console.WriteLine(Convert.ToString(n, 2));
            }
        }

        static void testAS()
        {
            string[] inf = { "mingjunhao", "39454007" };
            string recv1, recv2, recv3;
            string myip = AuthTool.GetInternalIP().ToString();
            IPAddress addr = IPAddress.Parse(myip);
            IPEndPoint ep = new IPEndPoint(addr, 8000);
            TcpClient client = new TcpClient();

            recv1 = Authentication.AS_Auth.Auth(inf,  8000, "shenruijie");
            //Console.WriteLine("收到的txt内容长度为" + recv1.Length);
            Console.WriteLine(recv1);
            recv2 = Authentication.TGS_Auth.Auth(recv1, inf[0], myip);
            Console.WriteLine(recv2);
            client.Connect(ep);
            recv3 = Authentication.V_Auth.Auth(recv2, inf[0], myip, client);
            Console.WriteLine(recv3);
            client.Close();
        }
    }
}
