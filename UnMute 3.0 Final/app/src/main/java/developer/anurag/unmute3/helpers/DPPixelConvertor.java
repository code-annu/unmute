package developer.anurag.unmute3.helpers;

import android.content.Context;

public class DPPixelConvertor {
    public static int dpToPixel(Context context,float dp){
        float density=context.getResources().getDisplayMetrics().density;
        return (int) (dp*density);
    }

    public static float pixelToDP(Context context,int pixel){
        float density=context.getResources().getDisplayMetrics().density;
        return  pixel/density;
    }
}
