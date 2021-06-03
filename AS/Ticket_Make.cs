using System;
using System.Collections.Generic;
using System.Text;


namespace AS
{
    class Ticket_Make
    {
        public static string MakeTicket(Tuple<int ,int>key, string uname,string uip,string idtgs)//票据生成
        {
            string pt;
            StringBuilder sb = new StringBuilder();

            sb.Append(key.Item1);
            sb.Append(",");
            sb.Append(key.Item2);
            sb.Append("%");
            sb.Append(uname);
            sb.Append("%");
            sb.Append(uip);
            sb.Append("%");
            sb.Append(idtgs);
            sb.Append("%");
            sb.Append(DateTime.Now.ToString());
            sb.Append("%");
            sb.Append(Program.Lifetime);

            pt = sb.ToString();
            return DES.Tool.txtDES(pt, true, "20181000");
            
        }
    }
}
