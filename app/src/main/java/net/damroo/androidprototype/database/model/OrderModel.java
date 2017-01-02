package net.damroo.androidprototype.database.model;


import android.net.Uri;

import com.google.gson.annotations.Expose;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.provider.ContentUri;
import com.raizlabs.android.dbflow.structure.provider.BaseSyncableProviderModel;
import com.raizlabs.android.dbflow.structure.provider.ContentUtils;

import net.damroo.androidprototype.database.MyDataBase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Table(database = MyDataBase.class)
public class OrderModel extends BaseSyncableProviderModel{

    public static final String NAME = "OrderModel";

    @ContentUri(path = NAME, type = ContentUri.ContentType.VND_MULTIPLE + NAME)
    public static final Uri CONTENT_URI = ContentUtils.buildUriWithAuthority(MyDataBase.AUTHORITY);

    @Override
    public Uri getDeleteUri() {
        return MyDataBase.OrderModel.CONTENT_URI;
    }

    @Override
    public Uri getInsertUri() {
        return MyDataBase.OrderModel.CONTENT_URI;
    }

    @Override
    public Uri getUpdateUri() {
        return MyDataBase.OrderModel.CONTENT_URI;
    }

    @Override
    public Uri getQueryUri() {
        return MyDataBase.OrderModel.CONTENT_URI;
    }


    @PrimaryKey
    @Expose
    @Column(name = "orderId")
    private String orderId;

    @Column
    @Expose
    private String orderNumber;

    @Column
    private String displayDate;

    @Column
    private String displayDateItems;

    @Column
    private String displayLocation;

    @Column
    private String displayPrice;

    @Column
    private String displayItems;

    @Column
    private String displayNameCity;

    @Column
    private String imageUrl;

    // DATES
    @Column
    @Expose
    private Date creationDate;

    @Column
    @Expose
    private Date invoicedOn;

    @Column
    @Expose
    private Date deliveredOn;

    @Column
    @Expose
    private Date pendingOn;

    @Column
    @Expose
    private Date archivedOn;

    @Column
    @Expose
    private Date dispatchedOn;

    @Column
    @Expose
    private Date viewedOn;

    @Column
    @Expose
    private Date rejectedOn;

    @Column
    @Expose
    private Date closedOn;

    @Column
    @Expose
    private Date paidOn;

    @Column
    @Expose
    private Date returnedOn;

    @Column
    @Expose
    private Date partialyDispatchedOn;

    @Column
    @Expose
    private Date partialyPaidOn;

    @Column
    @Expose
    private Date partialyInvoicedOn;

    @Column
    @Expose
    private Date inProcessOn;

    @Column
    @Expose
    private Date readyForDispatchOn;


    @Expose
    private BillingAddress billingAddress;

    @Expose
    private ShippingAddress shippingAddress;

    @Expose
    private LineItemContainer lineItemContainer;

    public OrderModel() {
    }

    public String getFormattedCreationDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, yyyy HH:mm", Locale.ENGLISH);
        return sdf.format(this.creationDate);
    }

    public String getFormattedCreationDateWithoutTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH);
        return sdf.format(this.creationDate);
    }

    @Override
    public void save() {
        this.setDisplayDate(getFormattedCreationDate());
        this.setDisplayDateItems(getFormattedCreationDateWithoutTime());
        if (billingAddress != null) {
            if (billingAddress.getDisplayCountry() != null)
                this.setDisplayLocation(this.billingAddress.getCity() + ", " + this.billingAddress.getDisplayCountry());
            else
                this.setDisplayLocation(this.billingAddress.getCity() + ", " + this.billingAddress.getCountry());
            this.setDisplayNameCity(this.billingAddress.getFirstName() + " " + this.billingAddress.getLastName() + ", " + this.billingAddress.getCity());
        }
        if (shippingAddress != null) {
            if (shippingAddress.getDisplayCountry() != null)
                this.setDisplayLocation(this.shippingAddress.getCity() + ", " + this.shippingAddress.getDisplayCountry());
            else
                this.setDisplayLocation(this.shippingAddress.getCity() + ", " + this.shippingAddress.getCountry());
            this.setDisplayNameCity(this.shippingAddress.getFirstName() + " " + this.shippingAddress.getLastName() + ", " + this.shippingAddress.getCity());
        }
        if (lineItemContainer != null) {
            this.setDisplayPrice(this.lineItemContainer.getGrandTotal().getFormatted());
            if (this.lineItemContainer.getProductLineItems().size() > 1)
                this.setDisplayItems(this.lineItemContainer.getProductLineItems().size() + " items");
            else
                this.setDisplayItems(this.lineItemContainer.getProductLineItems().size() + " item");
            this.setDisplayDateItems(this.getDisplayItems()+", "+this.getDisplayDateItems());
            List<Image> images = this.lineItemContainer.getProductLineItems().get(0).getImages();
            for (Image image : images) {
                if (image.getClassifier().equalsIgnoreCase("Small")) {
                    this.setImageUrl(image.getUrl());
                }
            }
        }
        super.save();
    }


    public String getDisplayDate() {
        return displayDate;
    }

    public void setDisplayDate(String displayDate) {
        this.displayDate = displayDate;
    }

    public String getDisplayLocation() {
        return displayLocation;
    }

    public void setDisplayLocation(String displayLocation) {
        this.displayLocation = displayLocation;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDisplayPrice() {
        return displayPrice;
    }

    public void setDisplayPrice(String displayPrice) {
        this.displayPrice = displayPrice;
    }

    public String getDisplayItems() {
        return displayItems;
    }

    public void setDisplayItems(String displayItems) {
        this.displayItems = displayItems;
    }

    public String getDisplayNameCity() {
        return displayNameCity;
    }

    public void setDisplayNameCity(String displayNameCity) {
        this.displayNameCity = displayNameCity;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getInvoicedOn() {
        return invoicedOn;
    }

    public void setInvoicedOn(Date invoicedOn) {
        this.invoicedOn = invoicedOn;
    }

    public Date getDeliveredOn() {
        return deliveredOn;
    }

    public void setDeliveredOn(Date deliveredOn) {
        this.deliveredOn = deliveredOn;
    }

    public Date getPendingOn() {
        return pendingOn;
    }

    public void setPendingOn(Date pendingOn) {
        this.pendingOn = pendingOn;
    }

    public Date getArchivedOn() {
        return archivedOn;
    }

    public void setArchivedOn(Date archivedOn) {
        this.archivedOn = archivedOn;
    }

    public Date getDispatchedOn() {
        return dispatchedOn;
    }

    public void setDispatchedOn(Date dispatchedOn) {
        this.dispatchedOn = dispatchedOn;
    }

    public Date getViewedOn() {
        return viewedOn;
    }

    public void setViewedOn(Date viewedOn) {
        this.viewedOn = viewedOn;
    }

    public Date getRejectedOn() {
        return rejectedOn;
    }

    public void setRejectedOn(Date rejectedOn) {
        this.rejectedOn = rejectedOn;
    }

    public Date getClosedOn() {
        return closedOn;
    }

    public void setClosedOn(Date closedOn) {
        this.closedOn = closedOn;
    }

    public Date getPaidOn() {
        return paidOn;
    }

    public void setPaidOn(Date paidOn) {
        this.paidOn = paidOn;
    }

    public Date getReturnedOn() {
        return returnedOn;
    }

    public void setReturnedOn(Date returnedOn) {
        this.returnedOn = returnedOn;
    }


    public String getDisplayDateItems() {
        return displayDateItems;
    }

    public void setDisplayDateItems(String displayDateItems) {
        this.displayDateItems = displayDateItems;
    }

    public Date getPartialyDispatchedOn() {
        return partialyDispatchedOn;
    }

    public void setPartialyDispatchedOn(Date partialyDispatchedOn) {
        this.partialyDispatchedOn = partialyDispatchedOn;
    }

    public Date getPartialyPaidOn() {
        return partialyPaidOn;
    }

    public void setPartialyPaidOn(Date partialyPaidOn) {
        this.partialyPaidOn = partialyPaidOn;
    }

    public Date getPartialyInvoicedOn() {
        return partialyInvoicedOn;
    }

    public void setPartialyInvoicedOn(Date partialyInvoicedOn) {
        this.partialyInvoicedOn = partialyInvoicedOn;
    }

    public Date getReadyForDispatchOn() {
        return readyForDispatchOn;
    }

    public void setReadyForDispatchOn(Date readyForDispatchOn) {
        this.readyForDispatchOn = readyForDispatchOn;
    }

    public Date getInProcessOn() {
        return inProcessOn;
    }

    public void setInProcessOn(Date inProcessOn) {
        this.inProcessOn = inProcessOn;
    }

    public BillingAddress getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(BillingAddress billingAddress) {
        this.billingAddress = billingAddress;
    }

    public ShippingAddress getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(ShippingAddress shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public LineItemContainer getLineItemContainer() {
        return lineItemContainer;
    }

    public void setLineItemContainer(LineItemContainer lineItemContainer) {
        this.lineItemContainer = lineItemContainer;
    }
}