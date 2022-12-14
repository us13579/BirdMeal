package com.ssafy.birdmeal.view.donation.nft

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.awesomedialog.*
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import com.ssafy.birdmeal.R
import com.ssafy.birdmeal.base.BaseFragment
import com.ssafy.birdmeal.databinding.FragmentCanvasBinding
import com.ssafy.birdmeal.utils.FileUtil
import com.ssafy.birdmeal.utils.TAG
import com.ssafy.birdmeal.utils.fileToBitmap
import com.ssafy.birdmeal.view.home.UserViewModel
import com.ssafy.birdmeal.view.loading.LoadingFragmentDialog
import com.ssafy.birdmeal.view.loading.LoadingFragmentDialog.Companion.loadingPhotoCardDialog


private val PERMISSIONS_REQUIRED = Manifest.permission.READ_EXTERNAL_STORAGE

class CanvasFragment : BaseFragment<FragmentCanvasBinding>(R.layout.fragment_canvas) {

    private val nftViewModel by activityViewModels<NFTViewModel>()
    private val userViewModel by activityViewModels<UserViewModel>()


    override fun init() {
        nftViewModel.setColor(Color.parseColor("#000000"))

        initViewModelCallBack()

        initClickListener()
    }

    private fun initViewModelCallBack() = with(binding) {
        nftViewModel.apply {
            color.observe(viewLifecycleOwner) {
                canvas.setPaintColor(it)
            }

            textMsgEvent.observe(viewLifecycleOwner) {
                canvas.addTextSticker(text?.value!!, color?.value!!, null)
            }

            fileMsgEvent.observe(viewLifecycleOwner) {
                loadingPhotoCardDialog.dismiss()
                showToast(it)
                completedPhotoCardDialog()
                findNavController().popBackStack()
            }
        }
    }

    private fun initClickListener() = with(binding) {
        // ?????? ??????
        ivCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        // NFT?????? ??????
        ivMakeNft.setOnClickListener {
            makePhotoCardDialog()
        }

        // undo ??????
        ivUndo.setOnClickListener {
            canvas.undo()
        }

        // redo ??????
        ivRedo.setOnClickListener {
            canvas.redo()
        }

        // palette ??????
        ivPalette.setOnClickListener {
            showPaletteDialog()
        }

        // text ??????
        ivText.setOnClickListener {
            nftViewModel.text.postValue("")
            TextDialog().show(childFragmentManager, "textDialog")
        }

        // photo ??????
//        ivPhoto.setOnClickListener {
//
//        }

        // gallery ??????
        ivGallery.setOnClickListener {
            requestPermissionLauncher.launch(PERMISSIONS_REQUIRED)
        }
    }

    // ???????????? ???????????? ???????????????
    private fun makePhotoCardDialog() {
        AwesomeDialog.build(requireActivity())
            .title("??????")
            .body("??????????????? ?????????????????????????")
            .icon(R.drawable.ic_photocard)
            .onPositive(text = "??????", buttonBackgroundColor = R.drawable.btn_round_10_green) {
                uploadPhotoCard()
            }
            .onNegative(text = "??????", buttonBackgroundColor = R.drawable.btn_round_main_color) {

            }
//        val positiveListener = {
//            loadingDialog.show(parentFragmentManager, "loadingDialog")
//            val bitmap = binding.canvas.downloadBitmap()
//            val multipartBody = FileUtil.bitmapToMultiPart(bitmap)
//            canvasViewModel.saveFile(multipartBody)
//            Unit
//        }
//        val negativeListener = {
//            Unit
//        }
//        YesOrNoDialog(
//            title = "????????? ?????? ???????????????\n?????????????????????????",
//            positive = "????????????",
//            negative = "??????",
//            positiveListener,
//            negativeListener
//        ).show(childFragmentManager, "YesOrNoDialog")
    }

    // ???????????? ?????? ????????? ???????????????
    private fun completedPhotoCardDialog() {
        AwesomeDialog.build(requireActivity())
            .title("??????")
            .body("??????????????? ?????????????????????")
            .icon(R.drawable.ic_photocard)
            .onNegative(text = "??????", buttonBackgroundColor = R.drawable.btn_round_10_green)
            .position(AwesomeDialog.POSITIONS.CENTER)
    }

    // ???????????? ??????
    private fun uploadPhotoCard() {
        LoadingFragmentDialog.loadingPhotoCardDialog.show(parentFragmentManager, "loadingDialog")
        val bitmap = binding.canvas.downloadBitmap()
        val multipartBody = FileUtil.bitmapToMultiPart(bitmap)
        nftViewModel.saveFile(multipartBody)
    }

    // ????????? ?????? ???????????????
    private fun showPaletteDialog() {
        ColorPickerDialog.Builder(requireContext())
            .setPreferenceName("platteDialog")
            .setPositiveButton("??????",
                object : ColorEnvelopeListener {
                    override fun onColorSelected(envelope: ColorEnvelope?, fromUser: Boolean) {
                        nftViewModel.setColor(envelope?.color ?: Color.parseColor("#000000"))
                    }
                }
            )
            .setNegativeButton("??????") { dialogInterface, _ -> dialogInterface?.dismiss() }
            .attachAlphaSlideBar(true) // the default value is true.
            .attachBrightnessSlideBar(true)  // the default value is true.
            .setBottomSpace(12) // set a bottom space between the last slidebar and buttons.
            .show()
    }

    val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // PERMISSION GRANTED
            pick()
        } else {
            // PERMISSION NOT GRANTED
            showToast("????????? ?????????")
        }
    }

    // ?????? ??????
    private fun pick() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        filterActivityLauncher.launch(intent)
    }

    // ?????? ????????? ????????? ??????
    private val filterActivityLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK && it.data != null) {

                val imagePath = it.data!!.data
                Log.d(TAG, "imagePath: $imagePath")

                val file = FileUtil.toFile(requireContext(), imagePath!!)
                Log.d(TAG, "file: $file")

                val bm = fileToBitmap(file)
                Log.d(TAG, "bm: $bm")

                binding.canvas.addBitmapSticker(bm!!)

            } else if (it.resultCode == Activity.RESULT_CANCELED) {
                showToast("?????? ?????? ??????")
            } else {
                Log.d(TAG, "filterActivityLauncher wrong")
            }
        }
}