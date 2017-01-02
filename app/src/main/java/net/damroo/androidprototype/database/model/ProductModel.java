package net.damroo.androidprototype.database.model;

import android.net.Uri;

import com.google.gson.annotations.Expose;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.provider.ContentUri;
import com.raizlabs.android.dbflow.structure.provider.BaseSyncableProviderModel;
import com.raizlabs.android.dbflow.structure.provider.ContentUtils;

import net.damroo.androidprototype.database.MyDataBase;

import java.util.List;

/**
 * Created by damroo on 1/1/2017.
 */
public class ProductModel extends BaseSyncableProviderModel {

    public static final String NAME = "ProductModel";

    @ContentUri(path = NAME, type = ContentUri.ContentType.VND_MULTIPLE + NAME)
    public static final Uri CONTENT_URI = ContentUtils.buildUriWithAuthority(MyDataBase.AUTHORITY);

    @Override
    public Uri getDeleteUri() {
        return MyDataBase.ProductModel.CONTENT_URI;
    }

    @Override
    public Uri getInsertUri() {
        return MyDataBase.ProductModel.CONTENT_URI;
    }

    @Override
    public Uri getUpdateUri() {
        return MyDataBase.ProductModel.CONTENT_URI;
    }

    @Override
    public Uri getQueryUri() {
        return MyDataBase.ProductModel.CONTENT_URI;
    }


    @PrimaryKey
    @Expose
    @Column(name = "productId")
    private String productId;

    @Column
    @Expose
    private String name;

    @Expose
    private List<Image> images;

    @Column
    private String image;


    @Override
    public void save(){
        for (Image image: images) {
            if(image.getClassifier().equalsIgnoreCase("Small")){
                this.setImage(image.getUrl());
            }
        }
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
