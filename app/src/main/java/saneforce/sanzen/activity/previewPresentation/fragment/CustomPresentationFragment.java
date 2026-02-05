package saneforce.sanzen.activity.previewPresentation.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

import saneforce.sanzen.activity.presentation.createPresentation.BrandMatrixModelClass;
import saneforce.sanzen.activity.presentation.createPresentation.brand.BrandMatrixAdapter;
import saneforce.sanzen.activity.presentation.createPresentation.brand.SpecialityNameAdapter;
import saneforce.sanzen.activity.presentation.createPresentation.SpecialityModelClass;
import saneforce.sanzen.activity.call.adapter.detailing.PlaySlideDetailing;
import saneforce.sanzen.activity.presentation.createPresentation.BrandModelClass;
import saneforce.sanzen.activity.presentation.createPresentation.brand.BrandNameAdapter;
import saneforce.sanzen.activity.presentation.createPresentation.selectedSlide.ItemTouchHelperCallBack;
import saneforce.sanzen.activity.presentation.createPresentation.selectedSlide.SelectedSlidesAdapter;
import saneforce.sanzen.activity.presentation.createPresentation.slide.ImageSelectionInterface;
import saneforce.sanzen.activity.presentation.createPresentation.slide.SlideImageAdapter;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.databinding.FragmentCustomPresentationBinding;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;

public class CustomPresentationFragment extends Fragment {
    private FragmentCustomPresentationBinding binding;
    private CommonUtilsMethods commonUtilsMethods;
    private RoomDB roomDB;
    private MasterDataDao masterDataDao;
    private BrandNameAdapter brandNameAdapter;
    private SlideImageAdapter slideImageAdapter;
    private SelectedSlidesAdapter selectedSlidesAdapter;
    private final ArrayList<BrandModelClass> brandProductArrayList = new ArrayList<>();
    public static ArrayList<BrandModelClass.Product> selectedSlideArrayList = new ArrayList<>();
    private final ArrayList<BrandModelClass.Product> savedPresentation = new ArrayList<>();
    private ImageSelectionInterface imageSelectionInterface;
    private ItemTouchHelper itemTouchHelper;
    private boolean isAllBrandsExpanded = false;
    private boolean isBrandMatrixExpanded = false;
    private boolean isSpecialityExpanded = false;


    private ArrayList<SpecialityModelClass> specialityArrayList = new ArrayList<>();
    private SpecialityNameAdapter SpecialityNameAdapter;
    private String selectedSpecialityCode = ""; // Keep track of selected speciality


    public CustomPresentationFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        roomDB = RoomDB.getDatabase(requireContext());
        masterDataDao = roomDB.masterDataDao();
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());
        selectedSlideArrayList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCustomPresentationBinding.inflate(inflater);
        uiInitialisation();
        loadSpecialitiesFromMaster();
        // changed 24/1== ALL BRANDS
        binding.clAllBrands.setOnClickListener(v -> {
            if (isAllBrandsExpanded) {
                binding.brandNameRecView.setVisibility(View.GONE);
                binding.imgAllBrandArrow.setRotation(0);
            } else {
                // Expand All Brands
                binding.brandNameRecView.setVisibility(View.VISIBLE);
                binding.imgAllBrandArrow.setRotation(180);
                binding.slideImageRecView.setVisibility(View.VISIBLE);
                binding.tvNoBrandMatrix.setVisibility(View.GONE);
                binding.imgBrandMatrixArrow.setRotation(0);
                isBrandMatrixExpanded = false;

//                binding.brandNameRecView.post(() -> {
//                    if (binding.brandNameRecView.getChildAt(0) != null) {
//                        binding.brandNameRecView.getChildAt(0).performClick();
//                    }
//                });
                binding.brandNameRecView.post(() -> {
                    binding.brandNameRecView.scrollToPosition(0);
                    binding.brandNameRecView.postDelayed(() -> {
                        RecyclerView.ViewHolder viewHolder =
                                binding.brandNameRecView.findViewHolderForAdapterPosition(0);

                        if (viewHolder != null) {
                            viewHolder.itemView.performClick();
                        }
                    }, 100);
                });

                //  AUTO-CLOSE SPECIALITY
                if (isSpecialityExpanded) {
                    binding.specialityRecView.setVisibility(View.GONE);
                    binding.tvNospeciality.setVisibility(View.GONE);
                    binding.imgspecialityArrow.setRotation(0);
                    isSpecialityExpanded = false;
                }
            }
            isAllBrandsExpanded = !isAllBrandsExpanded;
        });

        // changed 24/1== BRAND MATRIX
        binding.brandMatrix.setOnClickListener(view -> {
            if (isBrandMatrixExpanded) {
                // Collapse
                binding.tvNoBrandMatrix.setVisibility(View.GONE);
                binding.imgBrandMatrixArrow.setRotation(0);
                binding.slideImageRecView.setVisibility(View.VISIBLE);
            } else {
                // Expand: Load the 14 filtered slides directly
                loadBrandMatrixData(); // 🚀 Pudhu logic trigger aagum

                binding.imgBrandMatrixArrow.setRotation(180);

                // AUTO-CLOSE logic
                if (isAllBrandsExpanded) {
                    binding.brandNameRecView.setVisibility(View.GONE);
                    binding.imgAllBrandArrow.setRotation(0);
                    isAllBrandsExpanded = false;
                }
                if (isSpecialityExpanded) {
                    binding.specialityRecView.setVisibility(View.GONE);
                    binding.tvNospeciality.setVisibility(View.GONE);
                    binding.imgspecialityArrow.setRotation(0);
                    isSpecialityExpanded = false;
                }
            }
            isBrandMatrixExpanded = !isBrandMatrixExpanded;
        });
//        binding.brandMatrix.setOnClickListener(view -> {
//            if (isBrandMatrixExpanded) {
//                binding.tvNoBrandMatrix.setVisibility(View.GONE);
//                binding.imgBrandMatrixArrow.setRotation(0);
//                binding.slideImageRecView.setVisibility(View.VISIBLE);
//            } else {
//                binding.tvNoBrandMatrix.setVisibility(View.VISIBLE);
//                binding.imgBrandMatrixArrow.setRotation(180);
//                binding.slideImageRecView.setVisibility(View.GONE);
//
//                binding.brandMatrixRecView.post(() -> {
//                    binding.brandMatrixRecView.scrollToPosition(0);
//                    binding.brandMatrixRecView.postDelayed(() -> {
//                        RecyclerView.ViewHolder viewHolder =
//                                binding.brandMatrixRecView.findViewHolderForAdapterPosition(0);
//
//                        if (viewHolder != null) {
//                            viewHolder.itemView.performClick();
//                        }
//                    }, 100);
//                });
//
//                //  AUTO-CLOSE ALL BRANDS
//                if (isAllBrandsExpanded) {
//                    binding.brandNameRecView.setVisibility(View.GONE);
//                    binding.imgAllBrandArrow.setRotation(0);
//                    isAllBrandsExpanded = false;
//                }
//
//                // AUTO-CLOSE SPECIALITY
//                if (isSpecialityExpanded) {
//                    binding.specialityRecView.setVisibility(View.GONE); // Mukkiyam
//                    binding.tvNospeciality.setVisibility(View.GONE);
//                    binding.imgspecialityArrow.setRotation(0);
//                    isSpecialityExpanded = false;
//                }
//            }
//            isBrandMatrixExpanded = !isBrandMatrixExpanded;
//        });

        // changed 24/1== SPECIALITY
        binding.speciality.setOnClickListener(view -> {
            if (isSpecialityExpanded) {
                // Collapse Speciality
                binding.specialityRecView.setVisibility(View.GONE);
                binding.tvNospeciality.setVisibility(View.GONE);
                binding.imgspecialityArrow.setRotation(0);
                binding.slideImageRecView.setVisibility(View.VISIBLE);
            } else {
                // Expand Speciality
                binding.specialityRecView.setVisibility(View.VISIBLE);
                binding.tvNospeciality.setVisibility(View.GONE);
                binding.imgspecialityArrow.setRotation(180);
                binding.slideImageRecView.setVisibility(View.VISIBLE);

//                binding.specialityRecView.post(() -> {
//                    if (binding.specialityRecView.getChildAt(0) != null) {
//                        binding.specialityRecView.getChildAt(0).performClick();
//                    }
//                });
//                binding.specialityRecView.post(() -> {
//                    RecyclerView.ViewHolder viewHolder =
//                            binding.specialityRecView.findViewHolderForAdapterPosition(0);
//
//                    if (viewHolder != null) {
//                        viewHolder.itemView.performClick();
//                    }
//                });
                binding.specialityRecView.post(() -> {
                    binding.specialityRecView.scrollToPosition(0);
                    binding.specialityRecView.postDelayed(() -> {
                        RecyclerView.ViewHolder viewHolder =
                                binding.specialityRecView.findViewHolderForAdapterPosition(0);
                        if (viewHolder != null) {
                            viewHolder.itemView.performClick();
                        }
                    }, 100);
                });


                //  AUTO-CLOSE ALL BRANDS
                if (isAllBrandsExpanded) {
                    binding.brandNameRecView.setVisibility(View.GONE);
                    binding.imgAllBrandArrow.setRotation(0);
                    isAllBrandsExpanded = false;
                }

                //  AUTO-CLOSE BRAND MATRIX
                if (isBrandMatrixExpanded) {
                    binding.tvNoBrandMatrix.setVisibility(View.GONE);
                    binding.imgBrandMatrixArrow.setRotation(0);
                    isBrandMatrixExpanded = false;
                }
            }
            isSpecialityExpanded = !isSpecialityExpanded;
        });


        // changed 22/1== ALL BRANDS
//        binding.clAllBrands.setOnClickListener(v -> {
//
//            if (isAllBrandsExpanded) {
//                // Collapse All Brands
//                binding.brandNameRecView.setVisibility(View.GONE);
//                binding.imgAllBrandArrow.setRotation(0);
//
//            } else {
//                // Expand All Brands
//                binding.brandNameRecView.setVisibility(View.VISIBLE);
//                binding.imgAllBrandArrow.setRotation(180);
//
//                // 🔵 SHOW slides back
//                binding.slideImageRecView.setVisibility(View.VISIBLE);
//
//                // Hide Brand Matrix empty msg
//                binding.tvNoBrandMatrix.setVisibility(View.GONE);
//                binding.imgBrandMatrixArrow.setRotation(0);
//                isBrandMatrixExpanded = false;
//
//                // 🔹 Collapse Speciality if open
//                if (isSpecialityExpanded) {
//                    binding.tvNospeciality.setVisibility(View.GONE);
//                    binding.imgspecialityArrow.setRotation(0);
//                    isSpecialityExpanded = false;
//                }
//            }
//
//            isAllBrandsExpanded = !isAllBrandsExpanded;
//        });
//
// ;
//
//        // changed 22/1==BRAND MATRIX
//        binding.brandMatrix.setOnClickListener(view -> {
//
//            if (isBrandMatrixExpanded) {
//
//                // Collapse Brand Matrix
//                binding.tvNoBrandMatrix.setVisibility(View.GONE);
//                binding.imgBrandMatrixArrow.setRotation(0);
//
//                // Show slides
//                binding.slideImageRecView.setVisibility(View.VISIBLE);
//
//            } else {
//
//                // Expand Brand Matrix
//                binding.tvNoBrandMatrix.setVisibility(View.VISIBLE);
//                binding.imgBrandMatrixArrow.setRotation(180);
//
//                // Hide slides
//                binding.slideImageRecView.setVisibility(View.GONE);
//
//                // 🔴 CLOSE ALL BRANDS
//                if (isAllBrandsExpanded) {
//                    binding.brandNameRecView.setVisibility(View.GONE);
//                    binding.imgAllBrandArrow.setRotation(0);
//                    isAllBrandsExpanded = false;
//                }
//
//                // 🔴 CLOSE SPECIALITY  ← THIS WAS MISSING ❌
//                if (isSpecialityExpanded) {
//                    binding.tvNospeciality.setVisibility(View.GONE);
//                    binding.imgspecialityArrow.setRotation(0);
//                    isSpecialityExpanded = false;
//                }
//
//                binding.playBtn.setEnabled(false);
//                binding.clearBtn.setEnabled(false);
//            }
//
//            isBrandMatrixExpanded = !isBrandMatrixExpanded;
//        });
//
//
//        // changed 22/1== SPECIALITY
//        binding.speciality.setOnClickListener(view -> {
//
//            if (isSpecialityExpanded) {
//                // Collapse Speciality
//                binding.tvNospeciality.setVisibility(View.GONE);
//                binding.imgspecialityArrow.setRotation(0);
//                binding.specialityRecView.setVisibility(View.VISIBLE);
//                // Show slides
//                binding.slideImageRecView.setVisibility(View.VISIBLE);
//
//            } else {
//                // Expand Speciality
//
//                binding.tvNospeciality.setVisibility(View.VISIBLE);
//                binding.imgspecialityArrow.setRotation(180);
//                binding.specialityRecView.setVisibility(View.GONE);
//                // Hide slides
//                binding.slideImageRecView.setVisibility(View.GONE);
//
//                // Collapse All Brands if open
//                if (isAllBrandsExpanded) {
//                    binding.brandNameRecView.setVisibility(View.GONE);
//                    binding.imgAllBrandArrow.setRotation(0);
//                    isAllBrandsExpanded = false;
//                }
//
//                // Collapse Brand Matrix if open
//                if (isBrandMatrixExpanded) {
//                    binding.tvNoBrandMatrix.setVisibility(View.GONE);
//                    binding.imgBrandMatrixArrow.setRotation(0);
//                    isBrandMatrixExpanded = false;
//                }
//
//                // Disable buttons for Speciality
//                binding.playBtn.setEnabled(false);
//                binding.clearBtn.setEnabled(false);
//            }
//
//            isSpecialityExpanded = !isSpecialityExpanded;
//        });


        binding.playBtn.setOnClickListener(view -> {
            if (!selectedSlideArrayList.isEmpty()) {
                Intent intent = new Intent(requireContext(), PlaySlideDetailing.class);
                String data = new Gson().toJson(selectedSlideArrayList);
                Bundle bundle = new Bundle();
                bundle.putString("slideBundle", data);
                bundle.putString("position", String.valueOf(0));
                intent.putExtra("bundle", bundle);
                startActivity(intent);
            }
        });
        binding.clearBtn.setOnClickListener(view -> {
            if (!selectedSlideArrayList.isEmpty()) {
                selectedSlideArrayList.clear();
                savedPresentation.clear();
                brandProductArrayList.clear();
                uiInitialisation();
            }
        });
        return binding.getRoot();
    }

    public void uiInitialisation() {
        try {
            JSONArray prodSlide = masterDataDao.getMasterDataTableOrNew(Constants.PROD_SLIDE).getMasterSyncDataJsonArray();
            JSONArray brandSlide = masterDataDao.getMasterDataTableOrNew(Constants.BRAND_SLIDE).getMasterSyncDataJsonArray();

            LinkedHashMap<String, LinkedHashMap<String, String>> brandToProductWithPriority = new LinkedHashMap<>();
            HashMap<String, LinkedHashMap<String, JSONObject>> brandToProducts = new HashMap<>();

            for (int i = 0; i < prodSlide.length(); i++) {
                JSONObject productObject = prodSlide.getJSONObject(i);
                String id = productObject.getString("SlideId");
                String code = productObject.getString("Code");
                if (brandToProducts.containsKey(code)) {
                    brandToProducts.get(code).put(id, productObject);
                } else {
                    LinkedHashMap<String, JSONObject> productData = new LinkedHashMap<>();
                    productData.put(id, productObject);
                    brandToProducts.put(code, productData);
                }
            }

            for (int i = 0; i < brandSlide.length(); i++) {
                JSONObject brandObject = brandSlide.getJSONObject(i);
                String brandCode = brandObject.getString("Product_Brd_Code");
                String priority = brandObject.getString("Priority");
                String id = brandObject.getString("ID");
                if (brandToProductWithPriority.containsKey(brandCode)) {
                    brandToProductWithPriority.get(brandCode).put(id, priority);
                } else {
                    LinkedHashMap<String, String> productsList = new LinkedHashMap<>();
                    productsList.put(id, priority);
                    brandToProductWithPriority.put(brandCode, productsList);
                }
            }

            for (String brandCode : brandToProductWithPriority.keySet()) {
                ArrayList<BrandModelClass.Product> productArrayList = new ArrayList<>();
                String brandName = "", code = "", slideId = "", fileName = "", slidePriority = "", priority = "";
                LinkedHashMap<String, String> productWithPriority = brandToProductWithPriority.get(brandCode);
                HashMap<String, JSONObject> products = brandToProducts.get(brandCode);
                if (productWithPriority != null) {
                    for (String productID : productWithPriority.keySet()) {
                        if (products != null && products.containsKey(productID)) {
                            JSONObject productObject = products.get(productID);
                            if (productObject != null) {
                                brandName = productObject.getString("Name");
                                BrandModelClass.Product product = getProductData(productObject, priority);
                                if (product != null) {
                                    productArrayList.add(product);
                                }
                            }
                        }
                    }
                    if (!productWithPriority.isEmpty() && products != null) {
                        for (String productID : productWithPriority.keySet()) {
                            products.remove(productID);
                        }
                    }
                }
                if (products != null && !products.isEmpty()) {
                    for (String productID : products.keySet()) {
                        JSONObject productObject = products.get(productID);
                        if (productObject != null) {
                            brandName = productObject.getString("Name");
                            BrandModelClass.Product product = getProductData(productObject, priority);
                            if (product != null) {
                                productArrayList.add(product);
                            }
                        }
                    }
                }
                if (!brandName.isEmpty() && !productArrayList.isEmpty()) {
                    BrandModelClass brandModelClass = new BrandModelClass(brandName, brandCode, priority, 0, false, productArrayList);
                    brandProductArrayList.add(brandModelClass);
                }
            }
            if (!brandProductArrayList.isEmpty()) {
                BrandModelClass brandModelClass = brandProductArrayList.get(0);
                brandModelClass.setBrandSelected(true);
                brandProductArrayList.set(0, brandModelClass);
            }

            if (!savedPresentation.isEmpty()) { //Changing image selection state if there are data in savedPresentation.same time load data of selected image to the SelectedSlideAdapter
                for (BrandModelClass.Product savedProduct : savedPresentation) {
                    for (BrandModelClass brandModelClass : brandProductArrayList) {
                        if (savedProduct.getBrandCode().equals(brandModelClass.getBrandCode())) {
                            for (BrandModelClass.Product product : brandModelClass.getProductArrayList()) {
                                if (savedProduct.getSlideId().equals(product.getSlideId())) {
                                    product.setImageSelected(true);
                                    selectedSlideArrayList.add(product); //selectedSlideArrayList is the array we pass to SelectedSlideAdapter
                                    break;
                                }
                            }
                        }
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        populateBrandNameAdapter(brandProductArrayList);
        populateSelectedSlideAdapter(selectedSlideArrayList, -1);
    }

    private BrandModelClass.Product getProductData(JSONObject productObject, String priority) {
        try {
            String brandName = productObject.getString("Name");
            String code = productObject.getString("Code");
            String slideId = productObject.getString("SlideId");
            String fileName = productObject.getString("FilePath");
            String slidePriority = productObject.getString("Priority");
            String productDetailCode = productObject.getString("Product_Detail_Code");
            if (priority.isEmpty()) priority = "500" + slidePriority;
            return new BrandModelClass.Product(code, brandName, slideId, fileName, priority, false, productDetailCode);
        } catch (Exception e) {
            Log.e("GetProductData", "getProductData: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void populateBrandNameAdapter(ArrayList<BrandModelClass> arrayList) {
        brandNameAdapter = new BrandNameAdapter(requireContext(), arrayList, (arrayList1, position) -> {
            populateSlideImageAdapter(arrayList1.get(position).getProductArrayList());
            brandNameAdapter.notifyDataSetChanged();
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext());
        binding.brandNameRecView.setLayoutManager(layoutManager);
        binding.brandNameRecView.setAdapter(brandNameAdapter);

        if (!arrayList.isEmpty())
            populateSlideImageAdapter(arrayList.get(0).getProductArrayList());
    }

    //    @SuppressLint("NotifyDataSetChanged")
//    public void populateSlideImageAdapter(ArrayList<BrandModelClass.Product> arrayList) {
//        imageSelectionInterface = (arrayList1, position) -> {
//            brandNameAdapter.notifyDataSetChanged();
//            slideImageAdapter.notifyDataSetChanged();
//            for (BrandModelClass brandModelClass : brandProductArrayList) {
//                for (BrandModelClass.Product product : brandModelClass.getProductArrayList()) {
//                    if (product.isImageSelected()) {
//                        if (!selectedSlideArrayList.isEmpty()) {
//                            for (int i = 0; i < selectedSlideArrayList.size(); i++) {
//                                if (!selectedSlideArrayList.get(i).getSlideId().equalsIgnoreCase(product.getSlideId())) {
//                                    selectedSlideArrayList.add(selectedSlideArrayList.size(), product);
//                                    break;
//                                }
//                            }
//                        } else {
//                            selectedSlideArrayList.add(selectedSlideArrayList.size(), product);
//                        }
//                    } else {
//                        if (!selectedSlideArrayList.isEmpty()) {
//                            for (int i = 0; i < selectedSlideArrayList.size(); i++) {
//                                if (selectedSlideArrayList.get(i).getSlideId().equalsIgnoreCase(product.getSlideId())) {
//                                    selectedSlideArrayList.remove(i);
//                                    break;
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//
//            int count = selectedSlideArrayList.size();
//            for (int i = 0; i < count; i++) {
//                for (int j = i + 1; j < count; j++) {
//                    if (selectedSlideArrayList.get(i).getSlideId().equalsIgnoreCase(selectedSlideArrayList.get(j).getSlideId())) {
//                        selectedSlideArrayList.remove(j--);
//                        count--;
//                    }
//                }
//            }
//
//            populateSelectedSlideAdapter(selectedSlideArrayList, position);
//            binding.playBtn.setEnabled(!selectedSlideArrayList.isEmpty());
//            binding.clearBtn.setEnabled(!selectedSlideArrayList.isEmpty());
//        };
//        slideImageAdapter = new SlideImageAdapter(requireContext(), arrayList, imageSelectionInterface);
//        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
//        binding.slideImageRecView.setLayoutManager(layoutManager);
//        binding.slideImageRecView.setAdapter(slideImageAdapter);
//        binding.playBtn.setEnabled(!selectedSlideArrayList.isEmpty());
//        binding.clearBtn.setEnabled(!selectedSlideArrayList.isEmpty());
//    }

    @SuppressLint("NotifyDataSetChanged")
    public void populateSlideImageAdapter(ArrayList<BrandModelClass.Product> arrayList) {


//        for (BrandModelClass.Product gridProduct : arrayList) {
//            for (BrandModelClass.Product selected : selectedSlideArrayList) {
//                if (gridProduct.getSlideId().equalsIgnoreCase(selected.getSlideId())) {
//                    gridProduct.setImageSelected(true);
//                    break;
//                }
//            }
//        }
        for (BrandModelClass.Product gridProduct : arrayList) {
            boolean isSelected = false;
            for (BrandModelClass.Product selected : selectedSlideArrayList) {
                if (gridProduct.getSlideId().equalsIgnoreCase(selected.getSlideId())) {
                    isSelected = true;
                    break;
                }
            }
            gridProduct.setImageSelected(isSelected);
        }

        imageSelectionInterface = (arrayList1, position) -> {
            //  Selection Sync
            for (BrandModelClass.Product product : arrayList1) {
                boolean alreadyExists = false;
                int existingIndex = -1;
                for (int i = 0; i < selectedSlideArrayList.size(); i++) {
                    if (selectedSlideArrayList.get(i).getSlideId().equalsIgnoreCase(product.getSlideId())) {
                        alreadyExists = true;
                        existingIndex = i;
                        break;
                    }
                }
                if (product.isImageSelected() && !alreadyExists) {
                    selectedSlideArrayList.add(product);
                } else if (!product.isImageSelected() && alreadyExists) {
                    selectedSlideArrayList.remove(existingIndex);
                }
            }

            // Automatic Global Sync (Brands & Specialities)
            updateAllSpecialityCounts();

            // Refresh UI
            if (brandNameAdapter != null) brandNameAdapter.notifyDataSetChanged();
            if (slideImageAdapter != null) slideImageAdapter.notifyDataSetChanged();
            if (SpecialityNameAdapter != null) SpecialityNameAdapter.notifyDataSetChanged();

            populateSelectedSlideAdapter(selectedSlideArrayList, position);

            binding.selectedSlideCount.setText(String.valueOf(selectedSlideArrayList.size()));
            binding.playBtn.setEnabled(!selectedSlideArrayList.isEmpty());
            binding.clearBtn.setEnabled(!selectedSlideArrayList.isEmpty());
        };

        slideImageAdapter = new SlideImageAdapter(requireContext(), arrayList, imageSelectionInterface);
        binding.slideImageRecView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.slideImageRecView.setAdapter(slideImageAdapter);
    }

    //    private void updateAllSpecialityCounts() {
//        if (specialityArrayList == null) return;
//
//        // 1. First, Brand counts-ai master selected list vachu update pannuvom
//        for (BrandModelClass brand : brandProductArrayList) {
//            int bCount = 0;
//            for (BrandModelClass.Product p : brand.getProductArrayList()) {
//                // Check global selection
//                boolean isSel = false;
//                for (BrandModelClass.Product s : selectedSlideArrayList) {
//                    if (p.getSlideId().equalsIgnoreCase(s.getSlideId())) {
//                        isSel = true;
//                        p.setImageSelected(true); // Automatic tick in other lists
//                        break;
//                    }
//                }
//                if (isSel) bCount++;
//                else p.setImageSelected(false);
//            }
//            brand.setSelectedSlideCount(bCount);
//        }
//
//
//        for (SpecialityModelClass spec : specialityArrayList) {
//            int specTotal = 0;
//
//
//            for (BrandModelClass brand : brandProductArrayList) {
//                for (BrandModelClass.Product p : brand.getProductArrayList()) {
//
//                    for (BrandModelClass.Product selected : selectedSlideArrayList) {
//                        if (p.getSlideId().equalsIgnoreCase(selected.getSlideId())) {
//
//                            specTotal = calculateCountForSpec(spec.getCode());
//                        }
//                    }
//                }
//            }
//            spec.setSelectedSlideCount(specTotal);
//        }
//    }
    private void updateAllSpecialityCounts() {

        if (specialityArrayList == null) return;

        // 🟢 1. BRAND COUNTS (from global selected list)
        for (BrandModelClass brand : brandProductArrayList) {

            int brandCount = 0;

            for (BrandModelClass.Product p : brand.getProductArrayList()) {

                boolean isSelected = false;
                for (BrandModelClass.Product s : selectedSlideArrayList) {
                    if (p.getSlideId().equalsIgnoreCase(s.getSlideId())) {
                        isSelected = true;
                        break;
                    }
                }

                p.setImageSelected(isSelected);
                if (isSelected) brandCount++;
            }

            brand.setSelectedSlideCount(brandCount);
        }

        // 🟢 2. SPECIALITY COUNTS (DIRECT & SAFE)
        for (SpecialityModelClass spec : specialityArrayList) {
            int count = calculateCountForSpec(spec.getCode());
            spec.setSelectedSlideCount(count);
        }
    }

    // Helper to calculate count for a specific speciality code based on global selection
    private int calculateCountForSpec(String specCode) {
        int count = 0;
        HashSet<String> selectedIds = new HashSet<>();
        for (BrandModelClass.Product s : selectedSlideArrayList) selectedIds.add(s.getSlideId());

        try {
            JSONArray splSlide = masterDataDao.getMasterDataTableOrNew(Constants.SPL_SLIDE).getMasterSyncDataJsonArray();
            for (int i = 0; i < splSlide.length(); i++) {
                JSONObject obj = splSlide.getJSONObject(i);
                if (obj.optString("Doc_Special_Code").equalsIgnoreCase(specCode)) {
                    if (selectedIds.contains(obj.optString("ID"))) {
                        count++;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    public void populateSelectedSlideAdapter(ArrayList<BrandModelClass.Product> arrayList, int position) {
        selectedSlidesAdapter = new SelectedSlidesAdapter(requireContext(), arrayList, imageSelectionInterface, viewHolder -> itemTouchHelper.startDrag(viewHolder));
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext());
        binding.slidesRecView.setLayoutManager(layoutManager);

        ItemTouchHelperCallBack itemTouchHelperCallBack = new ItemTouchHelperCallBack(selectedSlidesAdapter);
        itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallBack);
        itemTouchHelper.attachToRecyclerView(binding.slidesRecView);
        binding.slideImageRecView.setHasFixedSize(false);
        binding.slidesRecView.setAdapter(selectedSlidesAdapter);
        binding.selectedSlideCount.setText(String.valueOf(arrayList.size()));
        if (position >= arrayList.size()) {
            binding.slidesRecView.scrollToPosition(arrayList.size() - 1);
        } else if (position >= 0) {
            binding.slidesRecView.scrollToPosition(position);
        }
    }

    private void loadSpecialitiesFromMaster() {
        try {
            JSONArray specialityMaster = masterDataDao
                    .getMasterDataTableOrNew(Constants.SPECIALITY)
                    .getMasterSyncDataJsonArray();

            specialityArrayList.clear();

            for (int i = 0; i < specialityMaster.length(); i++) {
                JSONObject obj = specialityMaster.getJSONObject(i);
                String code = obj.getString("Code");
                String name = obj.getString("Name");
                String docSpecialName = obj.getString("Doc_Special_Name");
                String divisionCode = obj.getString("Division_Code");

                SpecialityModelClass speciality = new SpecialityModelClass(code, name, docSpecialName, divisionCode);
                specialityArrayList.add(speciality);
            }

            if (!specialityArrayList.isEmpty()) {
                selectedSpecialityCode = specialityArrayList.get(0).getCode(); // select first by default
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        populateSpecialityAdapter();
    }


    private void populateSpecialityAdapter() {


        SpecialityNameAdapter = new SpecialityNameAdapter(requireContext(), specialityArrayList, (speciality, position) -> {
            selectedSpecialityCode = speciality.getCode(); // update selected speciality
            //    loadProductsForSelectedSpeciality();          // reload slides for this speciality
            loadSlidesForSpeciality(selectedSpecialityCode);
            SpecialityNameAdapter.notifyDataSetChanged();
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        binding.specialityRecView.setLayoutManager(layoutManager);
        binding.specialityRecView.setAdapter(SpecialityNameAdapter);
    }

    private void loadSlidesForSpeciality(String specialityCode) {
        ArrayList<BrandModelClass.Product> slidesForSpeciality = new ArrayList<>();

        try {
            JSONArray splSlide = masterDataDao
                    .getMasterDataTableOrNew(Constants.SPL_SLIDE)
                    .getMasterSyncDataJsonArray();

            HashSet<String> allowedSlideCodes = new HashSet<>();

            for (int i = 0; i < splSlide.length(); i++) {
                JSONObject obj = splSlide.getJSONObject(i);

                if (obj.optString("Doc_Special_Code").equalsIgnoreCase(specialityCode)) {
                    allowedSlideCodes.add(obj.optString("ID")); // 🔥 KEY
                }
            }
            HashSet<String> uniqueSlideCheckSet = new HashSet<>();

            for (BrandModelClass brand : brandProductArrayList) {

                for (BrandModelClass.Product product : brand.getProductArrayList()) {

                    String slideId = product.getSlideId();
                    if (slideId == null) continue;

                    if (allowedSlideCodes.contains(slideId)
                            && !uniqueSlideCheckSet.contains(slideId)) {

                        slidesForSpeciality.add(product);
                        uniqueSlideCheckSet.add(slideId);

                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // ✅ Ippo 10 + 2 = 12 slides thaan varum
        Log.d("CheckData", "Speciality: " + specialityCode);
        Log.d("CheckData", "Final Unique Slides count: " + slidesForSpeciality.size());

        populateSlideImageAdapter(slidesForSpeciality);
    }

    private void loadBrandMatrixData() {
        try {

            String currentDocMappedSlides = saneforce.sanzen.activity.previewPresentation.PreviewActivity.SlideCode;

            Log.d("Matrix_Check", "Current Doctor Mapped Codes: " + currentDocMappedSlides);

            if (currentDocMappedSlides == null || currentDocMappedSlides.isEmpty()) {
                binding.tvNoBrandMatrix.setVisibility(View.VISIBLE);
                return;
            }

            // Comma-ah vachu split panni list-ah mathurom for easy comparison
            List<String> selectedCodesList = Arrays.asList(currentDocMappedSlides.split(","));

            JSONArray prodSlide = masterDataDao.getMasterDataTableOrNew(Constants.PROD_SLIDE).getMasterSyncDataJsonArray();
            JSONArray brandSlide = masterDataDao.getMasterDataTableOrNew(Constants.BRAND_SLIDE).getMasterSyncDataJsonArray();

            // 2. Priority Map (IDs and Brand check)
            HashMap<String, String[]> priorityMap = new HashMap<>();
            for (int i = 0; i < brandSlide.length(); i++) {
                JSONObject bObj = brandSlide.getJSONObject(i);
                String brdCode = bObj.optString("Product_Brd_Code", "");
                if (brdCode.equals("9") || brdCode.equals("4")) {
                    priorityMap.put(bObj.getString("ID"), new String[]{bObj.getString("Priority"), brdCode});
                }
            }

            ArrayList<BrandModelClass.Product> matrixSlides = new ArrayList<>();
            HashSet<String> uniqueChecker = new HashSet<>();

            // 3. Loop Master - Strictly match with Current Doctor's codes
            for (int j = 0; j < prodSlide.length(); j++) {
                JSONObject pObj = prodSlide.getJSONObject(j);
                String sId = pObj.optString("SlideId", "");
                String pDetailCode = pObj.optString("Product_Detail_Code", ""); // Inga dhaan multiple codes irukum

                if (priorityMap.containsKey(sId) && !uniqueChecker.contains(sId)) {

                    // MAPPED DOCTOR CHECK:
                    // Oru slide-oda Product_Detail_Code, namma select panna doctor-oda code-la match aaganum
                    boolean isMatchFound = false;
                    for (String codeInSlide : pDetailCode.split(",")) {
                        if (selectedCodesList.contains(codeInSlide.trim())) {
                            isMatchFound = true;
                            break;
                        }
                    }

                    if (isMatchFound) {
                        String[] mapData = priorityMap.get(sId);
                        BrandModelClass.Product product = getProductData(pObj, mapData[0]);
                        if (product != null) {
                            matrixSlides.add(product);
                            uniqueChecker.add(sId);
                            Log.d("Matrix_Matched", "Adding Slide for Selected Doc: " + sId);
                        }
                    }
                }
            }

            // 4. Sort by Priority
            Collections.sort(matrixSlides, (p1, p2) -> {
                try {
                    return Integer.compare(Integer.parseInt(p1.getPriority()), Integer.parseInt(p2.getPriority()));
                } catch (Exception e) {
                    return 0;
                }
            });

            // 5. UI Update
            if (!matrixSlides.isEmpty()) {
                binding.tvNoBrandMatrix.setVisibility(View.GONE);
                binding.slideImageRecView.setVisibility(View.VISIBLE);
                populateSlideImageAdapter(matrixSlides);
            } else {
                binding.tvNoBrandMatrix.setVisibility(View.VISIBLE);
                binding.slideImageRecView.setVisibility(View.GONE);
                Log.d("Matrix_Empty", "No slides found for this specific doctor selection.");
            }

        } catch (Exception e) {
            Log.e("Matrix_Error", "Exception: " + e.getMessage());
        }
    }
//    private void loadBrandMatrixData() {
//        try {
//            Log.d("Matrix_Filter", "Starting Strict 7+7 Filter...");
//
//            // 1. Master Data Fetching
//            JSONArray prodSlide = masterDataDao.getMasterDataTableOrNew(Constants.PROD_SLIDE).getMasterSyncDataJsonArray();
//            JSONArray brandSlide = masterDataDao.getMasterDataTableOrNew(Constants.BRAND_SLIDE).getMasterSyncDataJsonArray();
//
//            // 2. ID vs Priority Map (Strictly for Code 9 and 4)
//            HashMap<String, String> priorityMap = new HashMap<>();
//            for (int i = 0; i < brandSlide.length(); i++) {
//                JSONObject bObj = brandSlide.getJSONObject(i);
//                String brdCode = bObj.optString("Product_Brd_Code", "");
//
//                // Priority list-layum brand match panni, andha IDs mattum edukirom
//                if (brdCode.equals("9") || brdCode.equals("4")) {
//                    priorityMap.put(bObj.getString("ID"), bObj.getString("Priority"));
//                }
//            }
//
//            // 3. Separate Buckets for Filtering
//            ArrayList<BrandModelClass.Product> blackSquardList = new ArrayList<>(); // Code 9
//            ArrayList<BrandModelClass.Product> dermDocList = new ArrayList<>();     // Code 4
//            HashSet<String> uniqueChecker = new HashSet<>();
//
//            for (int j = 0; j < prodSlide.length(); j++) {
//                JSONObject pObj = prodSlide.getJSONObject(j);
//                String bCode = pObj.getString("Code");
//                String sId = pObj.getString("SlideId");
//
//                // Priority table-la intha ID irundha mattum dhaan ulla varum
//                if (priorityMap.containsKey(sId) && !uniqueChecker.contains(sId)) {
//                    String finalPriority = priorityMap.get(sId);
//                    BrandModelClass.Product product = getProductData(pObj, finalPriority);
//
//                    if (product != null) {
//                        if (bCode.equals("9")) {
//                            blackSquardList.add(product);
//                        } else if (bCode.equals("4")) {
//                            dermDocList.add(product);
//                        }
//                        uniqueChecker.add(sId);
//                    }
//                }
//            }
//
//            // 4. Individual Sorting by Priority
//            Comparator<BrandModelClass.Product> prioritySorter = (p1, p2) -> {
//                try {
//                    return Integer.compare(Integer.parseInt(p1.getPriority()), Integer.parseInt(p2.getPriority()));
//                } catch (Exception e) {
//                    return 0;
//                }
//            };
//            Collections.sort(blackSquardList, prioritySorter);
//            Collections.sort(dermDocList, prioritySorter);
//
//            // 5. Final Compilation: Strictly Take Top 7 Mapped Items from each
//            ArrayList<BrandModelClass.Product> final14Slides = new ArrayList<>();
//
//            // Take 7 from Black Squard (Strictly Mapped)
//            for (int i = 0; i < Math.min(7, blackSquardList.size()); i++) {
//                final14Slides.add(blackSquardList.get(i));
//            }
//
//            // Take 7 from DermDoc (Strictly Mapped)
//            for (int i = 0; i < Math.min(7, dermDocList.size()); i++) {
//                final14Slides.add(dermDocList.get(i));
//            }
//
//            Log.d("Matrix_Final", "Strict Filtered Count: " + final14Slides.size());
//
//            // 6. UI Update
//            if (!final14Slides.isEmpty()) {
//                binding.tvNoBrandMatrix.setVisibility(View.GONE);
//                binding.slideImageRecView.setVisibility(View.VISIBLE);
//                populateSlideImageAdapter(final14Slides);
//            } else {
//                binding.tvNoBrandMatrix.setVisibility(View.VISIBLE);
//                binding.slideImageRecView.setVisibility(View.GONE);
//            }
//
//        } catch (Exception e) {
//            Log.e("Matrix_Exception", "Error: " + e.getMessage());
//        }
//    }
}