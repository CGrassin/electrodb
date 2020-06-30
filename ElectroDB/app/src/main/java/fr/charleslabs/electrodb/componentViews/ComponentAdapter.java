package fr.charleslabs.electrodb.componentViews;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import fr.charleslabs.electrodb.R;
import fr.charleslabs.electrodb.component.Component;

public class ComponentAdapter extends ArrayAdapter<Component> {

    public ComponentAdapter(Context context, List<Component> list){
        super(context,0,list);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent){

        //Set up the view
        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listitem_component,parent,false);

        ComponentViewHolder viewHolder = (ComponentViewHolder)convertView.getTag();
        if(viewHolder==null){
            viewHolder = new ComponentViewHolder();
            viewHolder.componentName = convertView.findViewById(R.id.resultList_ComponentName);
            viewHolder.componentDescription = convertView.findViewById(R.id.resultList_ComponentDescription);
            viewHolder.componentPicture = convertView.findViewById(R.id.resultList_componentPicture);
            convertView.setTag(viewHolder);
        }

        //Fetch the item in the component list
        Component comp = getItem(position);

        //Fill up the view
        viewHolder.componentName.setText(comp.getName());
        viewHolder.componentDescription.setText(comp.getDescription());
        switch (comp.getHousingType()){
            case Component.packageValve:
                viewHolder.componentPicture.setImageResource(R.drawable.resultlist_valve); break;
            case Component.packageDisplay:
                viewHolder.componentPicture.setImageResource(R.drawable.resultlist_display); break;
            case Component.packageOscillator:
                viewHolder.componentPicture.setImageResource(R.drawable.resultlist_xtal); break;

            case Component.packageArduino:
                viewHolder.componentPicture.setImageResource(R.drawable.resultlist_arduino); break;
            case Component.packageNucleo:
                viewHolder.componentPicture.setImageResource(R.drawable.resultlist_nucleo); break;
            case Component.packageRaspPi:
                viewHolder.componentPicture.setImageResource(R.drawable.resultlist_raspberry); break;
            case Component.packageESP:
                viewHolder.componentPicture.setImageResource(R.drawable.resultlist_esp8266); break;

            case Component.packageDIP:
            case Component.packageSIP:
                viewHolder.componentPicture.setImageResource(R.drawable.resultlist_dip); break;

            case Component.packageQFP:
            case Component.packagePLCC:
                viewHolder.componentPicture.setImageResource(R.drawable.resultlist_qfp); break;

            case Component.packageMLPQ:
            case Component.packageCSP:
            case Component.packageQFN:
            case Component.packageLLP:
            case Component.packageLLC:
            case Component.packageSON:
            case Component.packageDFN:
            case Component.packageMLF:
                viewHolder.componentPicture.setImageResource(R.drawable.resultlist_qfn); break;

            case Component.packageMELF:
            case Component.packageSMA:
            case Component.packageSMB:
            case Component.packageSMC:
            case Component.packageSOD:
                viewHolder.componentPicture.setImageResource(R.drawable.resultlist_smd); break;

            case Component.packageSOT23:
            case Component.packageSOT:
                viewHolder.componentPicture.setImageResource(R.drawable.resultlist_sot23); break;
            case Component.packageSOT223:
            case Component.packageSOT89:
                viewHolder.componentPicture.setImageResource(R.drawable.resultlist_sot223); break;

            case Component.packageTO220:
                viewHolder.componentPicture.setImageResource(R.drawable.resultlist_to220); break;
            case Component.packageTO92:
            case Component.packageTO:
                viewHolder.componentPicture.setImageResource(R.drawable.resultlist_to92); break;
            case Component.packageTO252:
            case Component.packageTO263:
                viewHolder.componentPicture.setImageResource(R.drawable.resultlist_dpak); break;

            case Component.packageSO:
                viewHolder.componentPicture.setImageResource(R.drawable.resultlist_so); break;

            case Component.packageBGA:
            case Component.packageLGA:
            case Component.packagePGA:
                viewHolder.componentPicture.setImageResource(R.drawable.resultlist_lga); break;

            default:
                viewHolder.componentPicture.setImageResource(R.drawable.resultlist_unknown);
        }

        //Return the view
        return convertView;
    }

    // View Holder
    private class ComponentViewHolder{
        TextView componentName;
        TextView componentDescription;
        ImageView componentPicture;
    }
}
