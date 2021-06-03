using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Net;
using System.Net.Sockets;
using System.IO;
using System.Threading;
using System.Text.RegularExpressions;


namespace AS
{
    class Program
    {
        public const int Lifetime = 2;//票据生存时间（时）
        static void Main(string[] args)
        {
            IPAddress addr = IPAddress.Parse("192.168.43.218");
            TcpListener lsn = new TcpListener(addr, 9000);
            lsn.Start();
            Console.WriteLine("Started in port:");
            while (true)
            {
                TcpClient client = lsn.AcceptTcpClient();
                Console.WriteLine("New client:{0}", client.GetHashCode());
                ThreadPool.QueueUserWorkItem(new WaitCallback(TcpClientProcess), client);
            }
            //string txt, ctxt, detxt;
            //txt = "12345678";
            //ctxt = DES.Tool.txtDES(txt, true, "39454007");
            //detxt = DES.Tool.txtDES(ctxt, false, "39454007");
            //Console.WriteLine(detxt);

            Console.ReadKey();
        }

        public static void TcpClientProcess(object state)
        {
            TcpClient client = state as TcpClient;
            int i, len;
            string lenstr, usr, code = null, idtgs, ts, msg, uip, uinf, txt;
            string[] pmsg, puinf;
            byte[] htemp = new byte[4], buf = new byte[8192];
            if (client == null)
                Console.WriteLine("Client is null!");
            NetworkStream ns = client.GetStream();
            //StreamWriter sw = new StreamWriter(ns);
            StreamReader sr = new StreamReader(ns);
            FileStream usf;

            while (true)
            {
                while (true)
                {
                    for (i = 0; i < 4; i++)
                    {
                        htemp[i] = (byte)sr.Read();
                    }
                    Console.WriteLine(htemp[1]);
                    if (htemp[0] > 16 || htemp[2] != 125)//报文类型错误或者ip地址错误
                        continue;
                    lenstr = (Convert.ToString(htemp[0], 2).PadLeft(8, '0') + Convert.ToString(htemp[1], 2).PadLeft(8, '0')).Substring(3);
                    Console.WriteLine("lenstr = " + lenstr);
                    uip = "192.168.43." + Convert.ToString(htemp[3]);
                    len = Convert.ToInt32(lenstr, 2);
                    Console.WriteLine("len = " + len);
                    for (i = 0; i < len; i++)
                    {
                        buf[i] = (byte)sr.Read();
                    }
                    break;
                }
                msg = Encoding.ASCII.GetString(buf).Substring(0, len);
                Console.WriteLine(Encoding.ASCII.GetString(buf).Substring(0, len));
                pmsg = msg.Split("%", 4);
                usr = pmsg[0];
                idtgs = pmsg[1];
                ts = pmsg[2];

                if (ChkTime(ts))
                {
                    Console.WriteLine("时间合法！\n");
                }
                else
                {
                    Console.WriteLine("时间不合法！\n");
                    continue;
                }

                usf = new FileStream("F:\\C#网络安全\\AS\\user.txt", FileMode.Open, FileAccess.Read);
                sr = new StreamReader(usf);
                while(sr.Peek()!=-1)
                {
                    uinf = sr.ReadLine();
                    puinf = uinf.Split("%", 2);
                    if(puinf[0].Equals(usr))
                    {
                        code = puinf[1];
                        break;
                    }
                }
                if (code == null)
                {
                    Console.WriteLine("没找到密码！\n");
                    break;
                    //continue;
                }
                Console.WriteLine("用户" + usr + "的密码是：" + code);
                SendToClient(client, usr, code, uip, idtgs);
                break;
            }
            //Console.WriteLine("buflen = " + Encoding.ASCII.GetString(buf).Length);
            //String s = "Welocome!";
            //while (!s.Equals("bye"))
            //{
            //    Console.WriteLine("Please input a string");
            //    s = Console.ReadLine();
            //    sw.WriteLine(s);
            //    sw.Flush();
            //}
            ////sw.WriteLine(s);
            //sw.Flush();
            //sw.Close();
            sr.Close();
            client.Close();
        }

        static bool ChkTime(string ts)
        {
            DateTime dnow = DateTime.Now, dts = DateTime.Parse(ts);
            TimeSpan span = dnow.Subtract(dts);
            int sec = (int)span.TotalSeconds;
            if (sec <= 1) return true;
            else 
                return false;
            
        }
        //获取向客户端发送的报文
        static void SendToClient(TcpClient client, string uname,string code, string uip, string idtgs)
        {
            int i;
            Random r = new Random();
            byte[] htemp, txttemp, result;
            string ticket, now, txt;
            short p = (short)(r.Next() % 100),q = (short)(r.Next()%100);
            StringBuilder sb = new StringBuilder();
            RSA.RSA_KeyMake rkey = new RSA.RSA_KeyMake(p, q);
            Tuple<int, int> pubk = rkey.GetPublicKey(), prik=rkey.GetPrivateKey();
            NetworkStream ns = client.GetStream();
            StreamWriter sw = new StreamWriter(ns);

            ticket = Ticket_Make.MakeTicket(prik, uname, uip, idtgs);
            now = DateTime.Now.ToString();

            sb.Append(pubk.Item1);
            sb.Append(",");
            sb.Append(pubk.Item2);
            sb.Append("%");
            sb.Append(idtgs);
            sb.Append("%");
            sb.Append(now);
            sb.Append("%");
            sb.Append(Lifetime);
            sb.Append("%");
            sb.Append(ticket);

            txt = DES.Tool.txtDES(sb.ToString(), true, code);
            txttemp = Encoding.UTF8.GetBytes(txt);
            htemp = GetHead(txttemp.Length, uip);
            result = new byte[txt.Length + 4];
            for(i = 0;i < 4;i++)
            {
                result[i] = htemp[i];
            }
            for(;i < txt.Length;i++)
            {
                result[i] = txttemp[i-4];
            }
            sw.Write(result);
            sw.Flush();
            sw.Close();
        }

        static byte[] GetHead(int tlen, string uip)
        {
            byte[] result = new byte[4];
            string headbstr;
            int l1, l2;//AS与本机的ip地址最后一位
            string myip = "192.168.43.218";//本机地址
            StringBuilder headbin = new StringBuilder();
            headbin.Append("001");//类型
            headbin.Append(Convert.ToString(tlen, 2).PadLeft(13, '0'));//长度
            Console.WriteLine("tlen = " + tlen);
            Console.WriteLine("bin of tlen = " + Convert.ToString(tlen, 2).PadLeft(13, '0'));

            l1 = Convert.ToInt32(myip.Split(".".ToCharArray(), 4)[3]);
            l2 = Convert.ToInt32(uip.Split(".".ToCharArray(), 4)[3]);
            headbin.Append(Convert.ToString(l1, 2).PadLeft(8, '0'));
            headbin.Append(Convert.ToString(l2, 2).PadLeft(8, '0'));

            headbstr = headbin.ToString();
            Console.WriteLine(headbstr.Length);
            for (int i = 0; i < 4; i++)
            {
                result[i] = Convert.ToByte(headbstr.Substring(i * 8, 8), 2);
            }

            return result;
        }
    }
}
